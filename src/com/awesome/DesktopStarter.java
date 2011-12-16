package com.awesome;

import com.awesome.srpg.SRPG;
import com.badlogic.gdx.backends.jogl.JoglApplication;

public class DesktopStarter {
	public static void main(String[] args) {
		new JoglApplication(new SRPG(),
							"title",
							720, 480, false);
	}
}
