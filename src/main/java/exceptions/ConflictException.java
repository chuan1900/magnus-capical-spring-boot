/**
 * @file ConflictException.java
 * @Author from PersonDemo project provided by Harry
 * @date July 25, 2018
 * @brief Exception for Service class
 *  
 */

package exceptions;

/**
 * Dedicated exception for ID conflicts.
 */
public  class ConflictException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private Object candidate;
	private int ID;

	public ConflictException(Object candidate, int ID) {
		super("There is already a " + candidate.getClass().getSimpleName() + " with ID " + ID + ".");

		this.candidate = candidate;
		this.ID = ID;
	}

	public Object getCandidate() {
		return candidate;
	}

	public int getID() {
		return ID;
	}
}