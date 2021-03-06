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

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.ManifoldPoint;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.ContactListener;

public class EdgeAndCircleContact extends Contact {
	private final Manifold m_manifold;
	private final ArrayList<Manifold> manifoldList = new ArrayList<>();
	private final CollideCircle collideCircle = new CollideCircle();

	EdgeAndCircleContact() {
		super();
		m_manifold = new Manifold();
		manifoldList.add(m_manifold);
		m_manifoldCount = 0;
	}

	private EdgeAndCircleContact(final Shape s1, final Shape s2) {
		super(s1, s2);
		assert(m_shape1.getType() == ShapeType.EDGE_SHAPE);
		assert(m_shape2.getType() == ShapeType.CIRCLE_SHAPE);
		m_manifold = new Manifold();
		manifoldList.add(m_manifold);
		m_manifoldCount = 0;
	}

	@Override
	public Contact clone() {
		assert false: "Not yet implemented.";
		return this;
	}

	// Locally reused stuff.
	transient private final Manifold m0 = new Manifold();
	transient private final Vec2 v1 = new Vec2();
	transient private final ContactPoint cp = new ContactPoint();

	@Override
	public void evaluate(final ContactListener listener) {
		final Body b1 = m_shape1.getBody();
		final Body b2 = m_shape2.getBody();

		m0.set(m_manifold);

		collideCircle.collideEdgeAndCircle(m_manifold, (EdgeShape)m_shape1, b1.getMemberXForm(), (CircleShape)m_shape2, b2.getMemberXForm());

		cp.shape1 = m_shape1;
		cp.shape2 = m_shape2;
		cp.friction = m_friction;
		cp.restitution = m_restitution;


		if (m_manifold.pointCount > 0) {
			m_manifoldCount = 1;
			final ManifoldPoint mp = m_manifold.points[0];

			if (m0.pointCount == 0) {
				mp.normalImpulse = 0.0f;
				mp.tangentImpulse = 0.0f;

				if (listener != null) {
					b1.getWorldLocationToOut(mp.localPoint1, cp.position);
					b1.getLinearVelocityFromLocalPointToOut(mp.localPoint1, v1);
					b2.getLinearVelocityFromLocalPointToOut(mp.localPoint2, cp.velocity);
					cp.velocity.subLocal(v1);

					cp.normal.set(m_manifold.normal);
					cp.separation = mp.separation;
					cp.id.set(mp.id);
					listener.add(cp);
				}
			} else {
				final ManifoldPoint mp0 = m0.points[0];
				mp.normalImpulse = mp0.normalImpulse;
				mp.tangentImpulse = mp0.tangentImpulse;

				if (listener != null) {
					b1.getWorldLocationToOut(mp.localPoint1, cp.position);
					b1.getLinearVelocityFromLocalPointToOut(mp.localPoint1, v1);
					b2.getLinearVelocityFromLocalPointToOut(mp.localPoint2, cp.velocity);
					cp.velocity.subLocal(v1);

					cp.normal.set(m_manifold.normal);
					cp.separation = mp.separation;
					cp.id.set(mp.id);
					listener.persist(cp);
				}
			}
		} else {
			m_manifoldCount = 0;
			if (m0.pointCount > 0 && (listener != null)) {
				final ManifoldPoint mp0 = m0.points[0];
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
	}

	@Override
	public List<Manifold> getManifolds() {
		return manifoldList;
	}

	public static Contact create(final Shape s1, final Shape s2) {
		// Fix order if incorrect.
		if (s1.m_type == ShapeType.EDGE_SHAPE
				&& s2.m_type == ShapeType.CIRCLE_SHAPE) {
			return new EdgeAndCircleContact(s1, s2);
		}else {
			return new EdgeAndCircleContact(s2, s1);
		}
	}
}
