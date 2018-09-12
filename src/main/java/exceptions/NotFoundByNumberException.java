/**
 * @file NotFoundByNumberException.java
 * @Author from PersonDemo project provided by Harry
 * @date July 25, 2018
 * @brief Exception number DNE for Service class
 *  
 */

package exceptions;

import magnuscapital.Trade;


public class NotFoundByNumberException extends NotFoundException {
	
	private static final long serialVersionUID = 1L;
	
	private int number;

	public NotFoundByNumberException(int number) {
		super(Trade.class, 0);
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	@Override
	public String getMessage() {
		return "No invoice with number " + number;
	}
}
