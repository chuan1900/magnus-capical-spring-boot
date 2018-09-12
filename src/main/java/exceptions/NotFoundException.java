/**
 * @file NotFoundException.java
 * @Author from PersonDemo project provided by Harry
 * @date July 25, 2018
 * @brief Exception entity DNE for Service class
 *  
 */

package exceptions;

public class NotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private Class<?> type;
	private Object ID;

	public NotFoundException(Class<?> type, Object ID) {
		super("No " + type.getSimpleName() + " found with ID " + ID + ".");

		this.type = type;
		this.ID = ID;
	}

	public Class<?> getType() {
		return type;
	}

	public Object getID() {
		return ID;
	}
}
