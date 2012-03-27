package org.eclipse.jt.core.impl;

import org.eclipse.jt.core.def.obja.StructClass;
import org.eclipse.jt.core.spi.monitor.PerformanceIndexDeclare;
import org.eclipse.jt.core.type.DataType;
import org.eclipse.jt.core.type.GUID;


@StructClass
public final class PerformanceIndexDefineImpl extends NamedDefineImpl implements
		PerformanceIndexDeclare {

	final GUID id;

	public final GUID getID() {
		return this.id;
	};

	PerformanceIndexDefineImpl(String name, DataType dataType,
			boolean isSequence) {
		super(name);
		this.id = GUID.MD5Of(name);
		this.dataType = dataType;
		this.isSequence = isSequence;
	}

	private static class CommandImpl extends NamedDefineImpl implements
			CommandDeclare {

		public CommandImpl(String name) {
			super(name);
		}

		@Override
		public String getXMLTagName() {
			throw new UnsupportedOperationException();
		}

	}

	private final NamedDefineContainerImpl<CommandImpl> commands = new NamedDefineContainerImpl<CommandImpl>();

	public final CommandImpl newCommand(String name) {
		final CommandImpl command = new CommandImpl(name);
		this.commands.add(command);
		return command;
	}

	public final NamedDefineContainerImpl<CommandImpl> getCommands() {
		return this.commands;
	}

	final DataType dataType;
	final boolean isSequence;

	public final boolean isSequence() {
		return this.isSequence;
	}

	public final DataType getDataType() {
		return this.dataType;
	}

	private boolean isUnderSession;

	public final boolean isUnderSession() {
		return this.isUnderSession;
	}

	public final void setIsUnderSession(boolean value) {
		this.isUnderSession = value;
	}

	// /////////////////////////////////////
	@Override
	public String getXMLTagName() {
		throw new UnsupportedOperationException();
	}
}
