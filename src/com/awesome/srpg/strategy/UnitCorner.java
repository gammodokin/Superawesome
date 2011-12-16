package com.awesome.srpg.strategy;

public enum UnitCorner {
	ENEMY {
		@Override
		public UnitCorner friend() {
			return ENEMY;
		}

		@Override
		public UnitCorner enemy() {
			return LEARNER;
		}
	}, LEARNER {
		@Override
		public UnitCorner friend() {
			return LEARNER;
		}

		@Override
		public UnitCorner enemy() {
			return ENEMY;
		}
	};

	public abstract UnitCorner friend();

	public abstract UnitCorner enemy();
}
