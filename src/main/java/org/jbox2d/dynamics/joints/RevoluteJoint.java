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

package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Mat22;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.TimeStep;

import java.io.Serializable;


//Updated to rev. 56->108 of b2RevoluteJoint.cpp/.h

//Point-to-point constraint
//C = p2 - p1
//Cdot = v2 - v1
//   = v2 + cross(w2, r2) - v1 - cross(w1, r1)
//J = [-I -r1_skew I r2_skew ]
//Identity used:
//w k % (rx i + ry j) = w * (-ry i + rx j)

//Motor constraint
//Cdot = w2 - w1
//J = [0 0 -1 0 0 1]
//K = invI1 + invI2

public class RevoluteJoint extends Joint implements Serializable {
	public final Vec2 m_localAnchor1;	// relative
	public final Vec2 m_localAnchor2;
	public final Vec2 m_pivotForce;
	public float m_motorForce;
	public float m_limitForce;
	public float m_limitPositionImpulse;

	public final Mat22 m_pivotMass;		// effective mass for point-to-point constraint.
	public float m_motorMass;	// effective mass for motor/limit angular constraint.

	public boolean m_enableMotor;
	public float m_maxMotorTorque;
	public float m_motorSpeed;

	public boolean m_enableLimit;
	public float m_referenceAngle;
	public float m_lowerAngle;
	public float m_upperAngle;
	public LimitState m_limitState;

	public RevoluteJoint(final RevoluteJointDef def) {
		super(def);
		m_localAnchor1 = def.localAnchor1.clone();
		m_localAnchor2 = def.localAnchor2.clone();
		m_referenceAngle = def.referenceAngle;

		m_pivotForce = new Vec2(0.0f, 0.0f);
		m_motorForce = 0.0f;
		m_limitForce = 0.0f;
		m_limitPositionImpulse = 0.0f;
		m_pivotMass = new Mat22();

		m_lowerAngle = def.lowerAngle;
		m_upperAngle = def.upperAngle;
		m_maxMotorTorque = def.maxMotorTorque;
		m_motorSpeed = def.motorSpeed;
		m_enableLimit = def.enableLimit;
		m_enableMotor = def.enableMotor;
	}

	// Locally-used temps
	private final Vec2
			r1 = new Vec2(),
			r2 = new Vec2();
	private final Mat22
			K1 = new Mat22(),
			K2 = new Mat22(),
			K3 = new Mat22();
	@Override
	public void initVelocityConstraints(final TimeStep step) {
		final Body b1 = m_body1;
		final Body b2 = m_body2;

		// Compute the effective mass matrix.
		r1.set(b1.getMemberLocalCenter());
		r2.set(b2.getMemberLocalCenter());
		r1.subLocal(m_localAnchor1).negateLocal();
		r2.subLocal(m_localAnchor2).negateLocal();
		Mat22.mulToOut(b1.m_xf.R, r1, r1);
		Mat22.mulToOut(b2.m_xf.R, r2, r2);

		// K    = [(1/m1 + 1/m2) * eye(2) - skew(r1) * invI1 * skew(r1) - skew(r2) * invI2 * skew(r2)]
		//      = [1/m1+1/m2     0    ] + invI1 * [r1.y*r1.y -r1.x*r1.y] + invI2 * [r1.y*r1.y -r1.x*r1.y]
		//        [    0     1/m1+1/m2]           [-r1.x*r1.y r1.x*r1.x]           [-r1.x*r1.y r1.x*r1.x]
		final float invMass1 = b1.m_invMass, invMass2 = b2.m_invMass;
		final float invI1 = b1.m_invI, invI2 = b2.m_invI;

		K1.col1.x = invMass1 + invMass2;	K1.col2.x = 0.0f;
		K1.col1.y = 0.0f;					K1.col2.y = invMass1 + invMass2;

		K2.col1.x =  invI1 * r1.y * r1.y;	K2.col2.x = -invI1 * r1.x * r1.y;
		K2.col1.y = -invI1 * r1.x * r1.y;	K2.col2.y =  invI1 * r1.x * r1.x;

		K3.col1.x =  invI2 * r2.y * r2.y;	K3.col2.x = -invI2 * r2.x * r2.y;
		K3.col1.y = -invI2 * r2.x * r2.y;	K3.col2.y =  invI2 * r2.x * r2.x;

		K1.addLocal(K2).addLocal(K3);
		K1.invertToOut(m_pivotMass);

		m_motorMass = 1.0f / (invI1 + invI2);

		if (!m_enableMotor) {
			m_motorForce = 0.0f;
		}

		if (m_enableLimit) {
			final float jointAngle = b2.m_sweep.a - b1.m_sweep.a - m_referenceAngle;
			if (MathUtils.abs(m_upperAngle - m_lowerAngle) < 2.0f * Settings.angularSlop) {
				m_limitState = LimitState.EQUAL_LIMITS;
			} else if (jointAngle <= m_lowerAngle) {
				if (m_limitState != LimitState.AT_LOWER_LIMIT) {
					m_limitForce = 0.0f;
				}
				m_limitState = LimitState.AT_LOWER_LIMIT;
			} else if (jointAngle >= m_upperAngle) {
				if (m_limitState != LimitState.AT_UPPER_LIMIT) {
					m_limitForce = 0.0f;
				}
				m_limitState = LimitState.AT_UPPER_LIMIT;
			}else {
				m_limitState = LimitState.INACTIVE_LIMIT;
				m_limitForce = 0.0f;
			}
		} else {
			m_limitForce = 0.0f;
		}

		if (step.warmStarting) {
			b1.m_linearVelocity.x -= step.dt * invMass1 * m_pivotForce.x;
			b1.m_linearVelocity.y -= step.dt * invMass1 * m_pivotForce.y;
			b1.m_angularVelocity -= step.dt * invI1 * (Vec2.cross(r1, m_pivotForce) + m_motorForce + m_limitForce);

			b2.m_linearVelocity.x += step.dt * invMass2 * m_pivotForce.x;
			b2.m_linearVelocity.y += step.dt * invMass2 * m_pivotForce.y;
			b2.m_angularVelocity += step.dt * invI2 * (Vec2.cross(r2, m_pivotForce) + m_motorForce + m_limitForce);
		} else {
			m_pivotForce.setZero();
			m_motorForce = 0.0f;
			m_limitForce = 0.0f;
		}

		m_limitPositionImpulse = 0.0f;
	}

	public final Vec2 m_lastWarmStartingPivotForce = new Vec2(0.0f,0.0f);

	// Locally-used temps
	private final Vec2
			temp1 = new Vec2(),
			temp2 = new Vec2(),
			temp3 = new Vec2();
	@Override
	public void solveVelocityConstraints(final TimeStep step) {
		final Body b1 = m_body1;
		final Body b2 = m_body2;

		r1.set(b1.getMemberLocalCenter());
		r2.set(b2.getMemberLocalCenter());
		r1.subLocal(m_localAnchor1).negateLocal();
		r2.subLocal(m_localAnchor2).negateLocal();
		Mat22.mulToOut(b1.m_xf.R, r1, r1);
		Mat22.mulToOut(b2.m_xf.R, r2, r2);

		// Solve point-to-point constraint
		Vec2.crossToOut(b1.m_angularVelocity, r1, temp1);
		Vec2.crossToOut(b2.m_angularVelocity, r2, temp2);
		temp2.subLocal(b1.m_linearVelocity).subLocal(temp1).addLocal(b2.m_linearVelocity);

		Mat22.mulToOut(m_pivotMass, temp2, temp3);
		temp3.mulLocal(-step.inv_dt);

		if (step.warmStarting) {
			m_pivotForce.addLocal(temp3);
			m_lastWarmStartingPivotForce.set(m_pivotForce);
		} else {
			m_pivotForce.set(m_lastWarmStartingPivotForce);
		}

		final Vec2 P = temp3.mulLocal(step.dt);

		b1.m_linearVelocity.x -= b1.m_invMass * P.x;
		b1.m_linearVelocity.y -= b1.m_invMass * P.y;
		b1.m_angularVelocity -= b1.m_invI * Vec2.cross(r1, P);

		b2.m_linearVelocity.x += b2.m_invMass * P.x;
		b2.m_linearVelocity.y += b2.m_invMass * P.y;
		b2.m_angularVelocity += b2.m_invI * Vec2.cross(r2, P);

		if (m_enableMotor && m_limitState != LimitState.EQUAL_LIMITS) {
			final float motorCdot = b2.m_angularVelocity - b1.m_angularVelocity - m_motorSpeed;
			float motorForce = -step.inv_dt * m_motorMass * motorCdot;
			final float oldMotorForce = m_motorForce;
			m_motorForce = MathUtils.clamp(m_motorForce + motorForce, -m_maxMotorTorque, m_maxMotorTorque);
			motorForce = m_motorForce - oldMotorForce;

			if (!step.warmStarting) {
				m_motorForce = oldMotorForce;
			}

			final float P2 = step.dt * motorForce;
			b1.m_angularVelocity -= b1.m_invI * P2;
			b2.m_angularVelocity += b2.m_invI * P2;
		}

		if (m_enableLimit && m_limitState != LimitState.INACTIVE_LIMIT) {
			final float limitCdot = b2.m_angularVelocity - b1.m_angularVelocity;
			float limitForce = -step.inv_dt * m_motorMass * limitCdot;

			if (m_limitState == LimitState.EQUAL_LIMITS) {
				m_limitForce += limitForce;
			} else if (m_limitState == LimitState.AT_LOWER_LIMIT) {
				final float oldLimitForce = m_limitForce;
				m_limitForce = MathUtils.max(m_limitForce + limitForce, 0.0f);
				limitForce = m_limitForce - oldLimitForce;
			} else if (m_limitState == LimitState.AT_UPPER_LIMIT) {
				final float oldLimitForce = m_limitForce;
				m_limitForce = MathUtils.min(m_limitForce + limitForce, 0.0f);
				limitForce = m_limitForce - oldLimitForce;
			}

			final float P2 = step.dt * limitForce;
			b1.m_angularVelocity -= b1.m_invI * P2;
			b2.m_angularVelocity += b2.m_invI * P2;
		}
	}

	// Local temps
	private final Vec2
			p1 = new Vec2(),
			p2 = new Vec2();
	@Override
	public boolean solvePositionConstraints() {
		final Body b1 = m_body1, b2 = m_body2;
		float positionError;

		// Solve point-to-point position error.
		r1.set(b1.getMemberLocalCenter());
		r2.set(b2.getMemberLocalCenter());
		r1.subLocal(m_localAnchor1).negateLocal();
		r2.subLocal(m_localAnchor2).negateLocal();
		Mat22.mulToOut(b1.m_xf.R, r1, r1);
		Mat22.mulToOut(b2.m_xf.R, r2, r2);

		p1.set(b1.m_sweep.c);
		p1.addLocal(r1);
		p2.set(b2.m_sweep.c);
		p2.addLocal(r2);
		temp1.set(p2);
		temp1.subLocal(p1);

		positionError = temp1.length();

		final float invMass1 = b1.m_invMass, invMass2 = b2.m_invMass;
		final float invI1 = b1.m_invI, invI2 = b2.m_invI;

		K1.col1.x = invMass1 + invMass2;    K1.col2.x = 0.0f;
		K1.col1.y = 0.0f;                   K1.col2.y = invMass1 + invMass2;

		K2.col1.x =  invI1 * r1.y * r1.y;   K2.col2.x = -invI1 * r1.x * r1.y;
		K2.col1.y = -invI1 * r1.x * r1.y;   K2.col2.y =  invI1 * r1.x * r1.x;

		K3.col1.x =  invI2 * r2.y * r2.y;   K3.col2.x = -invI2 * r2.x * r2.y;
		K3.col1.y = -invI2 * r2.x * r2.y;   K3.col2.y =  invI2 * r2.x * r2.x;

		final Mat22 K = K1.addLocal(K2).addLocal(K3);
		K.solveToOut(temp1.negateLocal(), temp2);

		b1.m_sweep.c.x -= b1.m_invMass * temp2.x;
		b1.m_sweep.c.y -= b1.m_invMass * temp2.y;
		b1.m_sweep.a -= b1.m_invI * Vec2.cross(r1, temp2);

		b2.m_sweep.c.x += b2.m_invMass * temp2.x;
		b2.m_sweep.c.y += b2.m_invMass * temp2.y;
		b2.m_sweep.a += b2.m_invI * Vec2.cross(r2, temp2);

		b1.synchronizeTransform();
		b2.synchronizeTransform();

		// Handle limits.
		float angularError = 0.0f;


		if (m_enableLimit && m_limitState != LimitState.INACTIVE_LIMIT) {
			final float angle = b2.m_sweep.a - b1.m_sweep.a - m_referenceAngle;
			float limitImpulse = 0.0f;

			if (m_limitState == LimitState.EQUAL_LIMITS) {
				// Prevent large angular corrections
				final float limitC = MathUtils.clamp(angle, -Settings.maxAngularCorrection, Settings.maxAngularCorrection);
				limitImpulse = -m_motorMass * limitC;
				angularError = MathUtils.abs(limitC);
			} else if (m_limitState == LimitState.AT_LOWER_LIMIT) {
				float limitC = angle - m_lowerAngle;
				angularError = MathUtils.max(0.0f, -limitC);

				// Prevent large angular corrections and allow some slop.
				limitC = MathUtils.clamp(limitC + Settings.angularSlop, -Settings.maxAngularCorrection, 0.0f);
				limitImpulse = -m_motorMass * limitC;
				final float oldLimitImpulse = m_limitPositionImpulse;
				m_limitPositionImpulse = MathUtils.max(m_limitPositionImpulse + limitImpulse, 0.0f);
				limitImpulse = m_limitPositionImpulse - oldLimitImpulse;
			} else if (m_limitState == LimitState.AT_UPPER_LIMIT) {
				float limitC = angle - m_upperAngle;
				angularError = MathUtils.max(0.0f, limitC);

				// Prevent large angular corrections and allow some slop.
				limitC = MathUtils.clamp(limitC - Settings.angularSlop, 0.0f, Settings.maxAngularCorrection);
				limitImpulse = -m_motorMass * limitC;
				final float oldLimitImpulse = m_limitPositionImpulse;
				m_limitPositionImpulse = MathUtils.min(m_limitPositionImpulse + limitImpulse, 0.0f);
				limitImpulse = m_limitPositionImpulse - oldLimitImpulse;
			}

			b1.m_sweep.a -= b1.m_invI * limitImpulse;
			b2.m_sweep.a += b2.m_invI * limitImpulse;

			b1.synchronizeTransform();
			b2.synchronizeTransform();
		}

		return positionError <= Settings.linearSlop && angularError <= Settings.angularSlop;
	}

	@Override
	public Vec2 getAnchor1() {
		return m_body1.getWorldLocation(m_localAnchor1);
	}
	public float getAnchor1XCoord() {
		return m_body1.getWorldXPosition(m_localAnchor1);
	}

	@Override
	public Vec2 getAnchor2() {
		return m_body2.getWorldLocation(m_localAnchor2);
	}

	@Override
	public Vec2 getReactionForce() {
		return m_pivotForce;
	}

	@Override
	public float getReactionTorque() {
		return m_limitForce;
	}

	public float getJointAngle() {
		final Body b1 = m_body1;
		final Body b2 = m_body2;
		return b2.m_sweep.a - b1.m_sweep.a - m_referenceAngle;
	}

	public float getJointSpeed() {
		final Body b1 = m_body1;
		final Body b2 = m_body2;
		return b2.m_angularVelocity - b1.m_angularVelocity;
	}

	public boolean isMotorEnabled() {
		return m_enableMotor;
	}

	public void enableMotor(final boolean flag) {
		m_enableMotor = flag;
	}

	public float getMotorTorque() {
		return m_motorForce;
	}

	public void setMotorSpeed(final float speed) {
		m_motorSpeed = speed;
	}

	public void setMaxMotorTorque(final float torque) {
		m_maxMotorTorque = torque;
	}

	public boolean isLimitEnabled() {
		return m_enableLimit;
	}

	public void enableLimit(final boolean flag) {
		m_enableLimit = flag;
	}

	public float getLowerLimit() {
		return m_lowerAngle;
	}

	public float getUpperLimit() {
		return m_upperAngle;
	}

	public void setLimits(final float lower, final float upper) {
		assert(lower <= upper);
		m_lowerAngle = lower;
		m_upperAngle = upper;
	}
}
