/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2014 - 2018 ImageJ developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.scijava.ops.matcher;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.scijava.ops.core.Op;
import org.scijava.util.Types;

/**
 * Data structure which identifies an op by name and/or type(s) and/or argument
 * type(s), along with a list of input arguments.
 * <p>
 * With the help of the {@link OpMatcher}, an {@code OpRef} holds all
 * information needed to create an appropriate {@link Op}.
 * </p>
 *
 * @author Christian Dietz (University of Konstanz)
 * @author Curtis Rueden
 */
public class OpRef {

	/** Name of the op, or null for any name. */
	private final String name;

	/** Types which the op must match. */
	private final Type[] types;

	/** The op's output parameter types, or null for no constraints. */
	private final Type outType;

	/** Arguments to be passed to the op. */
	private final Type[] args;

	// -- Static construction methods --

	public static OpRef fromTypes(final Type[] types, final Type outType, final Type... args) {
		return new OpRef(null, filterNulls(types), outType, filterNulls(args));
	}

	public static OpRef fromTypes(final String name, final Type[] types, final Type outType, final Type... args) {
		return new OpRef(name, filterNulls(types), outType, filterNulls(args));
	}

	// -- Constructor --

	/**
	 * Creates a new op reference.
	 *
	 * @param name
	 *            name of the op, or null for any name.
	 * @param types
	 *            types which the ops must match.
	 * @param outType
	 *            the op's required output type.
	 * @param args
	 *            arguments to the op.
	 */
	public OpRef(final String name, final Type[] types, final Type outType, final Type[] args) {
		this.name = name;
		this.types = types;
		this.outType = outType;
		this.args = args;
	}

	// -- OpRef methods --

	/** Gets the name of the op. */
	public String getName() {
		return name;
	}

	/** Gets the types which the op must match. */
	public Type[] getTypes() {
		return types.clone();
	}

	/**
	 * Gets the op's output type constraint, or null for no constraint.
	 */
	public Type getOutType() {
		return outType;
	}

	/** Gets the op's arguments. */
	public Type[] getArgs() {
		return args.clone();
	}

	/**
	 * Gets a label identifying the op's scope (i.e., its name and/or types).
	 */
	public String getLabel() {
		final StringBuilder sb = new StringBuilder();
		append(sb, name);
		if (types != null) {
			for (final Type t : types) {
				append(sb, Types.name(t));
			}
		}
		return sb.toString();
	}

	public boolean typesMatch(final Type opType) {
		return typesMatch(opType, new HashMap<>());
	}

	/**
	 * Determines whether the specified type satisfies the op's required types
	 * using {@link Types#isApplicable(Type[], Type[])}.
	 */
	public boolean typesMatch(final Type opType, final Map<TypeVariable<?>, Type> typeVarAssigns) {
		if (types == null)
			return true;
		for (Type t : types) {
			if(t instanceof ParameterizedType) {
				if (!MatchingUtils.checkGenericAssignability(opType, (ParameterizedType) t, typeVarAssigns, true)) {
					return false;
				}
			} else {
				if (!Types.isAssignable(opType, t)) {
					return false;
				}
			}
		}
		return true;
	}

	// -- Object methods --

	@Override
	public String toString() {
		String n = name == null ? "" : "Name: \"" + name + "\", Types: ";
		n += Arrays.deepToString(types) + "\n";
		n += "Input Types: \n";
		for (Type arg : args) {
			n += "\t\t* ";
			n += arg == null ? "" : arg.getTypeName();
			n += "\n";
		}
		n += "Output Type: \n";
		n += "\t\t* ";
		n += outType == null ? "" : outType.getTypeName();
		n += "\n";
		return n.substring(0, n.length() - 1);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OpRef other = (OpRef) obj;
		if (!Objects.equals(name, other.name))
			return false;
		if (!Objects.equals(types, other.types))
			return false;
		if (!Objects.equals(outType, other.outType))
			return false;
		if (!Objects.equals(args, other.args))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, types, outType, args);
	}

	// -- Utility methods --

	public static Type[] filterNulls(final Type... types) {
		Type[] ts = Arrays.stream(types).filter(t -> t != null).toArray(Type[]::new);
		return ts == null ? null : ts;
	}

	// -- Helper methods --

	private void append(final StringBuilder sb, final String s) {
		if (s == null)
			return;
		if (sb.length() > 0)
			sb.append("/");
		sb.append(s);
	}
}
