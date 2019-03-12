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

package org.jbox2d.collision;

import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;

import java.io.Serializable;

//Updated to rev 56->139 of b2Collision.h

/** An oriented bounding box. */
public class OBB implements Serializable {
	/** The rotation matrix. */
	public final Mat22 R;
	/** The local centroid. */
	public final Vec2 center;
	/** The half-widths. */
	public final Vec2 extents;

	/**
	 * creates OBB with the given data.  The arguments are cloned
	 * before being placed in the object
	 * @param _R
	 * @param _center
	 * @param _extents
	 */
	public OBB(final Mat22 _R, final Vec2 _center, final Vec2 _extents) {
		R = _R.clone();
		center = _center.clone();
		extents = _extents.clone();
	}

	/**
	 * copies from the given OBB
	 * @param copy
	 */
	public OBB(final OBB copy) {
		this(copy.R, copy.center, copy.extents);
	}

	public OBB() {
		R = new Mat22();
		center = new Vec2();
		extents = new Vec2();
	}

	/**
	 * @return a copy
	 */
	@Override
	public OBB clone() {
		return new OBB(this);
	}
}