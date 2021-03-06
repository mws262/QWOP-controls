/*
 * JBox2D - A Java Port of Erin Catto's Box2D
 * 
 * JBox2D homepage: http://jbox2d.sourceforge.net/
 * Box2D homepage: http://www.box2d.org
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 * claim that you wrote the original software. If you use this software
 * in a product, an acknowledgment in the product documentation would be
 * appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package org.jbox2d.dynamics.contacts;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.ContactID;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.ManifoldPoint;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.ContactListener;

//Updated to rev 144 of b2PolyAndCircleContact.h/cpp
class PolyAndCircleContact extends Contact {

	private Manifold m_manifold = new Manifold();
	private ArrayList<Manifold> manifoldList = new ArrayList<>();
	private CollideCircle collideCircle = new CollideCircle();

	// Locally reused stuff.
//	transient private Manifold m0 = new Manifold();
//	transient private Vec2 v1 = new Vec2();
//	transient private ContactPoint cp = new ContactPoint();
	private Manifold m0 = new Manifold();
	private Vec2 v1 = new Vec2();
	private ContactPoint cp = new ContactPoint();

	private PolyAndCircleContact(final Shape s1, final Shape s2) {
		super(s1, s2);
		assert (m_shape1.getType() == ShapeType.POLYGON_SHAPE);
		assert (m_shape2.getType() == ShapeType.CIRCLE_SHAPE);
		manifoldList.add(m_manifold);
		m_manifoldCount = 0;
	}

	// For deserializing.
	public PolyAndCircleContact() {}

	@Override
	public Contact clone() {
		final PolyAndCircleContact newC = new PolyAndCircleContact(this.m_shape1,
		                                                           this.m_shape2);
		newC.m_manifold.set(this.m_manifold);
		newC.m_manifoldCount = this.m_manifoldCount;
		// The parent world.
		newC.m_world = this.m_world;

		// World pool and list pointers.
		newC.m_prev = this.m_prev;
		newC.m_next = this.m_next;

		// Nodes for connecting bodies.
		newC.m_node1.set(m_node1);
		newC.m_node2.set(m_node2);

		// Combined friction
		newC.m_friction = this.m_friction;
		newC.m_restitution = this.m_restitution;

		newC.m_flags = this.m_flags;
		return newC;
	}

	public static Contact create(final Shape shape1, final Shape shape2) {
		if (shape1.m_type == ShapeType.POLYGON_SHAPE
				&& shape2.m_type == ShapeType.CIRCLE_SHAPE) {
			return new PolyAndCircleContact(shape1, shape2);
		}else {
			return new PolyAndCircleContact(shape2, shape1);
		}
	}

	@Override
	public List<Manifold> getManifolds() {
		return manifoldList;
	}

	@Override
	public void evaluate(final ContactListener listener) {
		final Body b1 = m_shape1.getBody();
		final Body b2 = m_shape2.getBody();

		m0.set(m_manifold);

		collideCircle.collidePolygonAndCircle(m_manifold, (PolygonShape)m_shape1, b1.getMemberXForm(),
				(CircleShape) m_shape2, b2.getMemberXForm());

		final boolean[] persisted = new boolean[] {false, false};

		cp.shape1 = m_shape1;
		cp.shape2 = m_shape2;
		cp.friction = m_friction;
		cp.restitution = m_restitution;

		// Match contact ids to facilitate warm starting.
		if (m_manifold.pointCount > 0) {
			// Match old contact ids to new contact ids and copy the
			// stored impulses to warm start the solver.
			for (int i = 0; i < m_manifold.pointCount; ++i) {
				final ManifoldPoint mp = m_manifold.points[i];
				mp.normalImpulse = 0.0f;
				mp.tangentImpulse = 0.0f;
				boolean found = false;
				final ContactID id = mp.id;

				for (int j = 0; j < m0.pointCount; ++j) {
					if (persisted[j]) {
						continue;
					}

					final ManifoldPoint mp0 = m0.points[j];

					if (mp0.id.isEqual(id)) {
						persisted[j] = true;
						mp.normalImpulse = mp0.normalImpulse;
						mp.tangentImpulse = mp0.tangentImpulse;

						// A persistent point.
						found = true;

						// Report persistent point.
						if (listener != null) {
							b1.getWorldLocationToOut(mp.localPoint1, cp.position);
							b1.getLinearVelocityFromLocalPointToOut(mp.localPoint1, v1);
							b2.getLinearVelocityFromLocalPointToOut(mp.localPoint2, cp.velocity);
							cp.velocity.subLocal(v1);

							cp.normal.set(m_manifold.normal);
							cp.separation = mp.separation;
							cp.id.set(id);
							listener.persist(cp);
						}
						break;
					}
				}

				// Report added point.
				if (!found && listener != null) {
					b1.getWorldLocationToOut(mp.localPoint1, cp.position);
					b1.getLinearVelocityFromLocalPointToOut(mp.localPoint1, v1);
					b2.getLinearVelocityFromLocalPointToOut(mp.localPoint2, cp.velocity);
					cp.velocity.subLocal(v1);

					cp.normal.set(m_manifold.normal);
					cp.separation = mp.separation;
					cp.id.set(id);
					listener.add(cp);
				}
			}

			m_manifoldCount = 1;
		} else {
			m_manifoldCount = 0;
		}

		if (listener == null) {
			return;
		}

		// Report removed points.
		for (int i = 0; i < m0.pointCount; ++i) {
			if (persisted[i]) {
				continue;
			}

			final ManifoldPoint mp0 = m0.points[i];
			b1.getWorldLocationToOut(mp0.localPoint1, cp.position);
			b1.getLinearVelocityFromLocalPointToOut(mp0.localPoint1, v1);
			b2.getLinearVelocityFromLocalPointToOut(mp0.localPoint2, cp.velocity);
			cp.velocity.subLocal(v1);
			cp.normal.set(m_manifold.normal);
			cp.separation = mp0.separation;
			cp.id.set(mp0.id);
			listener.remove(cp);
		}
	}
//
//	@Override
//	public void writeExternal(ObjectOutput out) throws IOException {
//		out.writeObject(m_manifold); // Manifold
//		out.writeObject(manifoldList); // ArrayList<Manifold>
//		out.writeObject(collideCircle); // CollideCircle
//	}
//
//	@Override
//	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
//		m_manifold = (Manifold) in.readObject();
//		manifoldList = (ArrayList<Manifold>) in.readObject();
//		collideCircle = (CollideCircle) in.readObject();
//
//		m0 = new Manifold();
//		v1 = new Vec2();
//		cp = new ContactPoint();
//	}
}