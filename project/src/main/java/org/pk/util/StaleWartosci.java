package org.pk.util;

import com.password4j.BcryptFunction;
import com.password4j.types.Bcrypt;

public class StaleWartosci {
	public static final BcryptFunction bcrypt = BcryptFunction.getInstance(Bcrypt.Y,10);
}
