package org.jbox2d.collision.shapes;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.MassData;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;

import java.io.Serializable;

/**
 * Point shape.  Like a circle shape of zero radius, except
 * that it has a finite mass.
 *
 */
public class PointShape extends Shape implements Serializable {
	public final Vec2 m_localPosition;
	public float m_mass;

	public PointShape(final ShapeDef def) {
		super(def);
		assert(def.type == ShapeType.POINT_SHAPE);
		final PointDef pointDef = (PointDef)def;
		m_type = ShapeType.POINT_SHAPE;
		m_localPosition = pointDef.localPosition.clone();
		m_mass = pointDef.mass;
	}

	/**
	 * @see Shape#computeAABB(AABB, XForm)
	 */
	@Override
	public void computeAABB(final AABB aabb, final XForm transform) {
		final Vec2 p = new Vec2();
		Mat22.mulToOut(transform.R, m_localPosition, p);
		p.add(transform.position);
		aabb.lowerBound.set(p.x-Settings.EPSILON, p.y-Settings.EPSILON);
		aabb.upperBound.set(p.x+Settings.EPSILON, p.y+Settings.EPSILON);
	}

	/**
	 * @see Shape#computeMass(MassData)
	 */
	@Override
	public void computeMass(final MassData massData) {
		massData.mass = m_mass;
		massData.center.set(m_localPosition);
		massData.I = 0.0f;
	}

	/**
	 * @see Shape#computeSweptAABB(AABB, XForm, XForm)
	 */
	@Override
	public void computeSweptAABB(final AABB aabb, final XForm transform1, final XForm transform2) {
		final Vec2 sweptP1 = new Vec2();
		final Vec2 sweptP2 = new Vec2();
		Mat22.mulToOut( transform2.R, m_localPosition, sweptP1);
		Mat22.mulToOut( transform2.R, m_localPosition, sweptP2);

		Vec2.minToOut( sweptP1, sweptP2, aabb.lowerBound);
		Vec2.maxToOut( sweptP1, sweptP2, aabb.upperBound);

		aabb.lowerBound.x -= Settings.EPSILON;
		aabb.lowerBound.y -= Settings.EPSILON;

		aabb.upperBound.x += Settings.EPSILON;
		aabb.upperBound.y += Settings.EPSILON;
	}

	/**
	 * @see Shape#testPoint(XForm, Vec2)
	 */
	@Override
	public boolean testPoint(final XForm xf, final Vec2 p) {
		// TODO djm: could use more optimization.
		// we could probably use bit shifting
		return false;
	}

	/**
	 * @see Shape#updateSweepRadius(Vec2)
	 */
	@Override
	public void updateSweepRadius(final Vec2 center) {
		final float dx = m_localPosition.x - center.x;
		final float dy = m_localPosition.y - center.y;
		m_sweepRadius = MathUtils.sqrt(dx*dx + dy*dy) - Settings.toiSlop;
	}

	/**
	 * @return a copy of local position
	 */
	public Vec2 getLocalPosition() {
		return m_localPosition.clone();
	}

	/**
	 * This is the member variable for the local position.
	 * Don't change this.
	 * @return
	 */
	public Vec2 getMemberLocalPosition(){
		return m_localPosition;
	}

	public float getMass() {
		return m_mass;
	}
}
