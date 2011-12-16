package com.awesome.script;

import java.io.Serializable;

import com.awesome.srpg.strategy.Status;
import com.awesome.srpg.strategy.UnusualStatus;

public abstract class Condition implements Serializable {
	public static class Always extends Condition{
		public Always() {
			super(null, 0);
		}

		@Override
		boolean underCondition(Status s) {
			return true;
		}

		@Override
		public String toString() {
			return "  ";
		}
	}
	public static class Greater extends Condition{
		public Greater(StatusType type, int c) {
			super(type, c);
		}

		@Override
		boolean underCondition(Status s) {
			return type.getParam(s) > c;
		}
	}
	public static class Less extends Condition{
		public Less(StatusType type, int c) {
			super(type, c);
		}

		@Override
		boolean underCondition(Status s) {
			return type.getParam(s) < c;
		}
	}

	public static class NotStatus extends Condition{
		private UnusualStatus us;

		public NotStatus(UnusualStatus us) {
			super(null, 0);
			this.us = us;
		}

		@Override
		boolean underCondition(Status s) {
			return !s.getUnusualStatus().equals(us);
		}

		@Override
		public String toString() {
			return us + "‚Å‚È‚¢";
		}
	}

	public static class IsStatus extends Condition{
		private UnusualStatus us;

		public IsStatus(UnusualStatus us) {
			super(null, 0);
			this.us = us;
		}

		@Override
		boolean underCondition(Status s) {
			return s.getUnusualStatus().equals(us);
		}

		@Override
		public String toString() {
			return us + "";
		}
	}

	StatusType type;
	int c;

	public Condition(StatusType type, int c) {
		this.type = type;
		this.c = c;
	}

	abstract boolean underCondition(Status s);

	enum StatusType implements Serializable {
		HP {
			@Override
			int getParam(Status s) {
				return s.getCurrentHp() * 100 / s.getMaxHp();
			}
		}, MP {
			@Override
			int getParam(Status s) {
				return s.getCurrentMp();
			}
		};

		abstract int getParam(Status s);
	}

	@Override
	public String toString() {
		return type + " " + this.getClass().getSimpleName() + " than " + c;
	}
}
