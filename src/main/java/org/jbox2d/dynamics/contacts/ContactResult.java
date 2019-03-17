package org.jbox2d.dynamics.contacts;

import org.jbox2d.collision.ContactID;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

// Updated to rev 139 of b2Contact.h

/** This structure is used to report contact point results. */
public class ContactResult {
	/** The first shape */
	public Shape shape1;
	/** The second shape */
	public Shape shape2;
	/** Position in world coordinates */
	public final Vec2 position = new Vec2();
	/** Points from shape1 to shape2 */
	public final Vec2 normal = new Vec2();
	/** The normal impulse applied to body2 */
	public float normalImpulse;
	/** The tangent impulse applied to body2 */
	public float tangentImpulse;
	/** The contact id identifies the features in contact */
	public final ContactID id = new ContactID();;
}
