/**
 *  Copyright 2025 Aaron Ragudos, Hanz Mapua, Peter Dela Cruz, Jerick Remo, Kurt Raneses, and the contributors of the project.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”),
 *  to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.hanzm_10.murico.swingapp.lib.validator;

import java.nio.CharBuffer;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

public class PasswordValidator {
	/**
	 * <ul>
	 * <li>Has a minimum of 8 characters {8,}</li>
	 * <li>At least one upper case English letter. (?=.*?[A-Z])</li>
	 * <li>At least one lower case English letter. (?=.*?[a-z])</li>
	 * <li>At least one digit. (?=.*?[0-9])</li>
	 * <li>At least one special character. (?=.*? [#?!@$%^&*-])</li>
	 * </ul>
	 */
	public static @NotNull final Pattern STRONG_PASSWORD = Pattern
			.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$");
	public static @NotNull final String STRONG_PASSWORD_ERROR_MESSAGE = "Password must be 8+ characters with upper, lower, number, and special character.";

	public static boolean isPasswordValid(@NotNull final char[] password, @NotNull final Pattern regex) {
		return regex.matcher(CharBuffer.wrap(password)).find();
	}
}
