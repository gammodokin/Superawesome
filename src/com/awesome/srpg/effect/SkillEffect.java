package com.awesome.srpg.effect;

import com.awesome.game.base.Animation;
import com.awesome.game.base.Screen;
import com.awesome.srpg.object.Unit;
import com.awesome.srpg.strategy.Damage;
import com.badlogic.gdx.math.Vector3;

public enum SkillEffect {
	MAGIC {
		@Override
		public Animation createEffect(Screen screen, Vector3 start, Vector3 end,
				float stageSpan, Unit target, Damage damage) {
			return new MagicEffect(screen, start, end, stageSpan, target, damage);
		}
	}, CURE {
		@Override
		public Animation createEffect(Screen screen, Vector3 start, Vector3 end,
				float stageSpan, Unit target, Damage damage) {
			return new CureEffect(screen, end, stageSpan, target, damage);
		}
	}, ATTACK {
		@Override
		public Animation createEffect(Screen screen, Vector3 start, Vector3 end,
				float stageSpan, Unit target, Damage damage) {
			return new DamageEffect(screen, end, stageSpan, target, damage);
		}
	};

	public abstract Animation createEffect(Screen screen, Vector3 start, Vector3 end, float stageSpan, Unit target, Damage damage);
}
