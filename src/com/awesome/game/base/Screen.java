/*
 * Copyright 2010 Mario Zechner (contact@badlogicgames.com), Nathan Sweet (admin@esotericsoftware.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.awesome.game.base;

import com.badlogic.gdx.Application;

/**
 * Interface for a game screen, e.g. main menu, game loop, game over screen and so on.
 * @author mzechner
 *
 */
public interface Screen {
	/**
	 * Called when the screen should update itself, e.g. continue a simulation etc.
	 *
	 * @param app the Application
	 */
	public void update (Application app);

	public void addActor(Actor act);

	public void removeActor(Actor act);

	/**
	 * Called by GdxInvaders to check whether the screen is done.
	 *
	 * @return nextScreen
	 */
	public Screen nextScreen();

	/**
	 * Cleans up all resources of the screen, e.g. meshes, textures etc.
	 */
	public void dispose ();
}
