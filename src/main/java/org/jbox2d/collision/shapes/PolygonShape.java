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

package org.jbox2d.collision.shapes;

import java.io.*;
import java.util.List;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.MassData;
import org.jbox2d.collision.OBB;
import org.jbox2d.collision.SupportsGenericDistance;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;

//Updated to rev 142 of b2Shape.cpp/.h / b2PolygonShape.cpp/.h

/** A convex polygon shape.  Create using Body.createShape(ShapeDef), not the constructor here. */
public class PolygonShape extends Shape implements SupportsGenericDistance, Externalizable {
	/** Dump lots of debug information. */
	private static final boolean m_debug = false;

	/** Local position of the shape centroid in parent body frame. */
	public Vec2 m_centroid;

	/** The oriented bounding box of the shape. */
	public OBB m_obb;
	/**
	 * The vertices of the shape.  Note: use getVertexCount(), not m_vertices.length, to get number of active vertices.
	 */
	public Vec2[] m_vertices;
	/**
	 * The normals of the shape.  Note: use getVertexCount(), not m_normals.length, to get number of active normals.
	 */
	public Vec2[] m_normals;
	/**
	 * The normals of the shape.  Note: use getVertexCount(), not m_coreVertices.length, to get number of active vertices.
	 */
	public Vec2[] m_coreVertices;

	/** Number of active vertices in the shape. */
	public int m_vertexCount;

	// Intermediate variables
	transient private Mat22 tempMat1 = new Mat22();
	transient private AABB tempAABB1 = new AABB();
	transient private AABB tempAABB2 = new AABB();
	transient private Vec2 temp1 = new Vec2();
	transient private Vec2 temp2 = new Vec2();
	transient private Vec2 temp3 = new Vec2();
	transient private Vec2 temp4 = new Vec2();

	// Exists for deserializing only.
	public PolygonShape() {}

	public PolygonShape(final ShapeDef def) {
		super(def);

		assert(def.type == ShapeType.POLYGON_SHAPE);

		m_type = ShapeType.POLYGON_SHAPE;
		final PolygonDef poly = (PolygonDef)def;

		m_vertexCount = poly.getVertexCount();
		m_vertices = new Vec2[m_vertexCount];
		m_normals = new Vec2[m_vertexCount];
		m_coreVertices = new Vec2[m_vertexCount];

		m_obb = new OBB();

		// Get the vertices transformed into the body frame.
		assert(3 <= m_vertexCount && m_vertexCount <= Settings.maxPolygonVertices);

		// Copy vertices.
		for (int i = 0; i < m_vertexCount; ++i) {
			m_vertices[i] = poly.vertices.get(i).clone();
		}

		// Compute normals. Ensure the edges have non-zero length.
		final Vec2 edge = new Vec2();
		for (int i = 0; i < m_vertexCount; ++i) {
			final int i2 = i + 1 < m_vertexCount ? i + 1 : 0;
			edge.set(m_vertices[i2]).subLocal(m_vertices[i]);
			assert(edge.lengthSquared() > Settings.EPSILON*Settings.EPSILON);
			m_normals[i] = Vec2.cross(edge, 1.0f);
			m_normals[i].normalize();
		}

		if (m_debug) {
			// Ensure the polygon is convex.
			for (int i = 0; i < m_vertexCount; ++i) {
				for (int j = 0; j < m_vertexCount; ++j) {
					// Don't check vertices on the current edge.
					if (j == i || j == (i + 1) % m_vertexCount) {
						continue;
					}
					// Your polygon is non-convex (it has an indentation).
					// Or your polygon is too skinny.
					assert( Vec2.dot(m_normals[i], m_vertices[j].sub(m_vertices[i])) < -Settings.linearSlop);
				}
			}

			// Ensure the polygon is counter-clockwise.
			for (int i = 1; i < m_vertexCount; ++i) {
				float cross = Vec2.cross(m_normals[i-1], m_normals[i]);

				// Keep asinf happy.
				cross = MathUtils.clamp(cross, -1.0f, 1.0f);

				// You have consecutive edges that are almost parallel on your polygon.
				// Or the polygon is not counter-clockwise.
				final float angle = (float)Math.asin(cross);
				assert(angle > Settings.angularSlop);
			}
		}

		// Compute the polygon centroid.
		m_centroid = PolygonShape.computeCentroid(poly.vertices);

		// Compute the oriented bounding box.
		PolygonShape.computeOBB(m_obb, m_vertices);

		final Vec2 v = new Vec2(), d = new Vec2();
		final Mat22 A = new Mat22();
		
		// Create core polygon shape by shifting edges inward.
		// Also compute the min/max radius for CCD.
		for (int i = 0; i < m_vertexCount; ++i) {
			final int i1 = i - 1 >= 0 ? i - 1 : m_vertexCount - 1;
			final int i2 = i;

			final Vec2 n1 = m_normals[i1];
			final Vec2 n2 = m_normals[i2];
			v.set(m_vertices[i]).subLocal(m_centroid);

			d.x = Vec2.dot(n1, v) - Settings.toiSlop;
			d.y = Vec2.dot(n2, v) - Settings.toiSlop;

			// Shifting the edge inward by b2_toiSlop should
			// not cause the plane to pass the centroid.

			// Your shape has a radius/extent less than b2_toiSlop.
			if ((d.x < 0.0f || d.y < 0.0f)) {
				System.out.println("Error, polygon extents less than b2_toiSlop, dumping details: ");
				System.out.println("temp1.x: "+d.x+"temp1.y: "+d.y);
				System.out.println("n1: "+n1+"; n2: "+n2);
				System.out.println("v: "+v);
			}
			assert(d.x >= 0.0f);
			assert(d.y >= 0.0f);
			A.col1.x = n1.x; A.col2.x = n1.y;
			A.col1.y = n2.x; A.col2.y = n2.y;
			m_coreVertices[i] = A.solve(d).addLocal(m_centroid);
		}

		if (m_debug) {
			System.out.println("\nDumping polygon shape...");
			System.out.println("Vertices: ");
			for (int i=0; i<m_vertexCount; ++i) {
				System.out.println(m_vertices[i]);
			}
			System.out.println("Core Vertices: ");
			for (int i=0; i<m_vertexCount; ++i) {
				System.out.println(m_coreVertices[i]);
			}
			System.out.println("Normals: ");
			for (int i=0; i<m_vertexCount; ++i) {
				System.out.println(m_normals[i]);
			}
			System.out.println("Centroid: "+m_centroid);
		}
	}

	/**
	 * @see Shape#updateSweepRadius(Vec2)
	 */
	@Override
	public void updateSweepRadius(final Vec2 center) {
		// Update the sweep radius (maximum radius) as measured from a local temp4 point.
		m_sweepRadius = 0.0f;
		for (int i = 0; i < m_vertexCount; ++i) {
			temp1.set(m_coreVertices[i]);
			temp1.subLocal(center);
			m_sweepRadius = MathUtils.max(m_sweepRadius, temp1.length());
		}
	}

	/**
	 * @see Shape#testPoint(XForm, Vec2)
	 */
	@Override
	public boolean testPoint(final XForm xf, final Vec2 p) {

		temp1.set(p);
		temp1.subLocal(xf.position);
		Mat22.mulTransToOut(xf.R, temp1, temp2);

		for (int i = 0; i < m_vertexCount; ++i) {
			temp1.set(temp2);
			temp1.subLocal( m_vertices[i]);
			final float dot = Vec2.dot(m_normals[i], temp1);

			if (dot > 0.0f) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get the support point in the given world direction.
	 * Use the supplied transform.
	 * @see SupportsGenericDistance#support(Vec2, XForm, Vec2)
	 */
	public void support(final Vec2 dest, final XForm xf, final Vec2 d) {
		Mat22.mulTransToOut(xf.R, d, temp1);

		int bestIndex = 0;
		float bestValue = Vec2.dot(m_coreVertices[0], temp1);
		for (int i = 1; i < m_vertexCount; ++i) {
			final float value = Vec2.dot(m_coreVertices[i], temp1);
			if (value > bestValue) {
				bestIndex = i;
				bestValue = value;
			}
		}
		XForm.mulToOut(xf, m_coreVertices[bestIndex], dest);
	}

	public static Vec2 computeCentroid(final List<Vec2> vs) {

		// TEMPS
		final Vec2 e1 = new Vec2();
		final Vec2 e2 = new Vec2();
		final Vec2 p1 = new Vec2();

		final int count = vs.size();
		assert(count >= 3);

		final Vec2 c = new Vec2();
		float area = 0.0f;

		final float inv3 = 1.0f / 3.0f;
		
		for (int i = 0; i < count; ++i) {
			// Triangle vertices.
			final Vec2 p2 = vs.get(i);
			final Vec2 p3 = i + 1 < count ? vs.get(i+1) : vs.get(0);

			e1.set(p2).subLocal(p1);
			e2.set(p3).subLocal(p1);

			final float D = Vec2.cross(e1, e2);

			final float triangleArea = 0.5f * D;
			area += triangleArea;

			// Area weighted centroid
			c.x += triangleArea * inv3 * (p1.x + p2.x + p3.x);
			c.y += triangleArea * inv3 * (p1.y + p2.y + p3.y);
		}

		// Centroid
		assert(area > Settings.EPSILON);
		c.mulLocal(1.0f / area);
		return c;
	}



	// http://www.geometrictools.com/Documentation/MinimumAreaRectangle.pdf
	public static void computeOBB(final OBB obb, final Vec2[] vs){
		// TEMPS
		final Vec2 ux = new Vec2(),
				uy = new Vec2(),
				lower = new Vec2(),
				upper = new Vec2(),
				d = new Vec2(),
				r = new Vec2(),
				center = new Vec2();

		final Vec2[] pRay = new Vec2[Settings.maxPolygonVertices + 1];

		final int count = vs.length;
		assert(count <= Settings.maxPolygonVertices);
		

		System.arraycopy(vs, 0, pRay, 0, count);
		pRay[count] = pRay[0];

		float minArea = Float.MAX_VALUE;
		
		for (int i = 1; i <= count; ++i){
			final Vec2 root = pRay[i-1];
			ux.set(pRay[i]);
			ux.subLocal(root);
			final float length = ux.normalize();
			assert(length > Settings.EPSILON);
			uy.x = -ux.y;
			uy.y = ux.x;
			lower.x = Float.MAX_VALUE;
			lower.y = Float.MAX_VALUE;
			upper.x = -Float.MAX_VALUE; // djm wouldn't this just be Float.MIN_VALUE?
			upper.y = -Float.MAX_VALUE;

			for (int j = 0; j < count; ++j) {
				d.set(pRay[j]);
				d.subLocal(root);
				r.x = Vec2.dot(ux, d);
				r.y = Vec2.dot(uy, d);
				Vec2.minToOut(lower, r, lower);
				Vec2.maxToOut(upper, r, upper);
			}

			final float area = (upper.x - lower.x) * (upper.y - lower.y);
			if (area < 0.95f * minArea){
				minArea = area;
				obb.R.col1.set(ux);
				obb.R.col2.set(uy);

				center.set(0.5f * (lower.x + upper.x), 0.5f * (lower.y + upper.y));
				Mat22.mulToOut(obb.R, center, obb.center);
				obb.center.addLocal(root);

				obb.extents.x = 0.5f * (upper.x - lower.x);
				obb.extents.y = 0.5f * (upper.y - lower.y);
			}
		}
		assert(minArea < Float.MAX_VALUE);
	}

	/**
	 * @see Shape#computeAABB(AABB, XForm)
	 */
	@Override
	public void computeAABB(final AABB aabb, final XForm xf) {
		Mat22.mulToOut(xf.R, m_obb.R, tempMat1);
		tempMat1.absLocal();
		Mat22.mulToOut(tempMat1, m_obb.extents, temp1);
		// we treat the lower bound like the position
		Mat22.mulToOut(xf.R, m_obb.center, aabb.lowerBound);
		aabb.lowerBound.addLocal(xf.position);
		aabb.upperBound.set(aabb.lowerBound);
		aabb.lowerBound.subLocal(temp1);
		aabb.upperBound.addLocal(temp1);
	}

	/**
	 * @see Shape#computeSweptAABB(AABB, XForm, XForm)
	 */
	@Override
	public void computeSweptAABB(final AABB aabb, final XForm transform1, final XForm transform2) {
		tempAABB1.lowerBound.set(0, 0);
		tempAABB2.lowerBound.set(0, 0);
		computeAABB(tempAABB1, transform1);
		computeAABB(tempAABB2, transform2);
		Vec2.minToOut(tempAABB1.lowerBound, tempAABB2.lowerBound, aabb.lowerBound);
		Vec2.maxToOut(tempAABB1.upperBound, tempAABB2.upperBound, aabb.upperBound);
	}

	@Override
	public void computeMass(final MassData massData) {
		computeMass(massData, m_density);
	}

	/**
	 * @see Shape#computeMass(MassData)
	 */
	public void computeMass(final MassData massData, float density) {
		// Polygon mass, centroid, and inertia.
		// Let rho be the polygon density in mass per unit area.
		// Then:
		// mass = rho * int(dA)
		// centroid.x = (1/mass) * rho * int(x * dA)
		// centroid.y = (1/mass) * rho * int(y * dA)
		// I = rho * int((x*x + y*y) * dA)
		//
		// We can compute these integrals by summing all the integrals
		// for each triangle of the polygon. To evaluateActionDistribution the integral
		// for a single triangle, we make a change of variables to
		// the (u,v) coordinates of the triangle:
		// x = x0 + e1x * u + e2x * v
		// y = y0 + e1y * u + e2y * v
		// where 0 <= u && 0 <= v && u + v <= 1.
		//
		// We integrate u from [0,1-v] and then v from [0,1].
		// We also need to use the Jacobian of the transformation:
		// D = cross(temp1, temp2)
		//
		// Simplification: triangle centroid = (1/3) * (p1 + p2 + p3)
		//
		// The rest of the derivation is handled by computer algebra.

		assert(m_vertexCount >= 3);

		float area = 0.0f;
		float I = 0.0f;

		final float k_inv3 = 1.0f / 3.0f;

		for (int i = 0; i < m_vertexCount; ++i) {
			// Triangle vertices.
			final Vec2 p1 = temp3;
			final Vec2 p2 = m_vertices[i];
			final Vec2 p3 = i + 1 < m_vertexCount ? m_vertices[i+1] : m_vertices[0];

			temp1.set(p2);
			temp1.subLocal(p1);

			temp2.set(p3);
			temp2.subLocal(p1);

			final float D = Vec2.cross(temp1, temp2);

			final float triangleArea = 0.5f * D;
			area += triangleArea;

			// Area weighted centroid
			temp4.x += triangleArea * k_inv3 * (p1.x + p2.x + p3.x);
			temp4.y += triangleArea * k_inv3 * (p1.y + p2.y + p3.y);

			final float px = p1.x, py = p1.y;
			final float ex1 = temp1.x, ey1 = temp1.y;
			final float ex2 = temp2.x, ey2 = temp2.y;

			final float intx2 = k_inv3 * (0.25f * (ex1*ex1 + ex2*ex1 + ex2*ex2) + (px*ex1 + px*ex2)) + 0.5f*px*px;
			final float inty2 = k_inv3 * (0.25f * (ey1*ey1 + ey2*ey1 + ey2*ey2) + (py*ey1 + py*ey2)) + 0.5f*py*py;

			I += D * (intx2 + inty2);
		}

		// Total mass
		massData.mass = density * area;

		// Center of mass
		assert(area > Settings.EPSILON);
		temp4.mulLocal(1.0f / area);
		massData.center.set(temp4);

		// Inertia tensor relative to the local origin.
		massData.I = I * density;
	}

	/** Get the first vertex and apply the supplied transform. */
	public void getFirstVertexToOut(final XForm xf, final Vec2 out) {
		XForm.mulToOut(xf, m_coreVertices[0], out);
	}

	/** Get the oriented bounding box relative to the parent body. */
	public OBB getOBB() {
		return m_obb.clone();
	}

	/** Get the local centroid relative to the parent body. */
	public Vec2 getCentroid() {
		return m_centroid.clone();
	}

	/** Get the number of vertices. */
	public int getVertexCount() {
		return m_vertexCount;
	}

	/** Get the vertices in local coordinates. */
	public Vec2[] getVertices() {
		return m_vertices;
	}

	/**
	 * Get the core vertices in local coordinates. These vertices
	 * represent a smaller polygon that is used for time of impact
	 * computations.
	 */
	public Vec2[] getCoreVertices()	{
		return m_coreVertices;
	}

	/** Get the edge normal vectors.  There is one for each vertex. */
	public Vec2[] getNormals() {
		return m_normals;
	}

	/** Get the centroid and apply the supplied transform. */
	public Vec2 centroid(final XForm xf) {
		return XForm.mul(xf, m_centroid);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);

		out.writeObject(m_centroid); // Vec2
		out.writeObject(m_obb); // OBB
		out.writeObject(m_vertices); // Vec2[]
		out.writeObject(m_normals); // Vec2[]
		out.writeObject(m_coreVertices); // Vec2[]
		out.writeInt(m_vertexCount); // int
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);

		m_centroid = (Vec2) in.readObject();
		m_obb = (OBB) in.readObject();
		m_vertices = (Vec2[]) in.readObject();
		m_normals = (Vec2[]) in.readObject();
		m_coreVertices = (Vec2[]) in.readObject();
		m_vertexCount = in.readInt();

		tempMat1 = new Mat22();
		tempAABB1 = new AABB();
		tempAABB2 = new AABB();
		temp1 = new Vec2();
		temp2 = new Vec2();
		temp3 = new Vec2();
		temp4 = new Vec2();
	}
}
