/**
 * @file ConflictByNumberException.java
 * @Author from PersonDemo project provided by Harry
 * @date July 25, 2018
 * @brief Exception number clash for Service class
 *  
 */

package exceptions;

import exceptions.ConflictException;
import magnuscapital.Trade;

public class ConflictByNumberException extends ConflictException {
	
	private static final long serialVersionUID = 1L;

	private int number;

	public ConflictByNumberException(int number) {
		super(Trade.class, 0);
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	@Override
	public String getMessage() {
		return "There is already an invoice with number " + number;
	}
}
