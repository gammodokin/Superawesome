package com.awesome.srpg.object;

import com.awesome.game.base.Actor;
import com.awesome.game.base.RenderUtil;
import com.awesome.game.base.Renderer;
import com.awesome.game.base.Screen;
import com.awesome.srpg.SRPG;
import com.awesome.srpg.effect.MoveEffect;
import com.awesome.srpg.effect.MagicEffect;
import com.awesome.srpg.logic.Coord;
import com.awesome.srpg.logic.Damage;
import com.awesome.srpg.logic.Skill;
import com.awesome.srpg.logic.Status;
import com.awesome.srpg.operation.UnitOperator;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;


public class Unit extends Actor {

	//	static final float SCALE = 2;
	//	static final short LINES = 16;
	//	static final float SPAN = SCALE / LINES;

	//	private float[] verts = {
	//			SPAN,	SPAN*1.2f,	0,
	//			0,		SPAN*1.2f,	0,
	//			0,		0,		0,
	//			SPAN,	0,		0,
	//	};
	//
	//	private short[] indices = {
	//			0, 1, 2,
	//			0, 2, 3
	//	};
	//
	//	private float[] texCoords = {
	//			1, 0,
	//			0, 0,
	//			0, 1,
	//			1, 1,
	//	};
	//
	//	private Mesh mesh;
	//
	//	private Texture texture;
	private Decal decal;

	protected Stage stage;
	protected int cx, cy;
	protected Vector3 realCoord;

	protected Status status;

	protected UnitOperator operator;

	protected StatusBar statusBar;

	public Unit(Screen screen, Stage stage, int cx, int cy, Status status, UnitOperator operator, double initCarge) {
		super(screen);

		this.stage = stage;

		this.status = status;
		status.setCharge(initCarge);

		this.operator = operator;
		//		operator = new SimpleAttackOperator(this, status);

		setPos(cx, cy);
		setRealCoord(stage.coordToReal(cx, cy));

		statusBar = new StatusBar(screen, this, stage.SPAN);
	}

	//	private static final String MESH_NAME = "generalUnit";
	private void initGraph() {
		//		mesh = new Mesh(true, 4, 6,
		//				new VertexAttribute(Usage.Position, 3, "a_position"),
		//				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord"));
		//
		//		float[] vertices = new float[4 * (3 + 2)];
		//		int vIdx = 0;
		//		int tIdx = 0;
		//		for(int i = 0; i < vertices.length;) {
		//			vertices[i++] = verts[vIdx++];
		//			vertices[i++] = verts[vIdx++];
		//			vertices[i++] = verts[vIdx++];
		//			vertices[i++] = texCoords[tIdx++];
		//			vertices[i++] = texCoords[tIdx++];
		//		}
		//
		//		mesh.setVertices(vertices);
		//		mesh.setIndices(indices);
		//
		//		ren.entry(MESH_NAME, mesh);
		//
		//		mesh = ren.getMesh(MESH_NAME);
		//		texture = ren.getTexture(status.getTexName());

		if(SRPG.VIEW) {
			Renderer ren = SRPG.getRenderer();
			ren.entry(status.getTexName());
			ren.entryDecal(status.getTexName(), stage.SPAN, stage.SPAN*1.2f);
			ren.addDecalToBatch(status.getTexName());
			decal = ren.getDecal(status.getTexName());
		}
	}

	public void setPos(int cx, int cy) {
		this.cx = cx;
		this.cy = cy;
		//		realCoord = stage.coordToReal(cx, cy);
		//		decal.setPosition(realCoord.x, realCoord.y + SPAN/2, realCoord.z);
	}

	public void setRealCoord(Vector3 rc) {
		realCoord = rc;

		if(SRPG.VIEW && decal != null)
			decal.setPosition(realCoord.x, realCoord.y + stage.SPAN/2, realCoord.z);
	}

	/**
	 * 移動アニメーションのスタート地点として現在の座標を使用するため、setPos()するまえに使用する。
	 * @param tx
	 * @param ty
	 * @param path
	 */
	public void setMove(int tx, int ty, Coord<Integer>[] path) {
		if(cx == tx && cy == ty)
			setRealCoord(stage.coordToReal(tx, ty));
		else {
			if(SRPG.VIEW)
				SRPG.getRenderer().getAnimationManager().startSerialAnimation(new MoveEffect(screen, this, cx, cy, tx, ty, path, stage.SPAN));
			else
				setRealCoord(stage.coordToReal(tx, ty));
		}
	}

	public void setAttack(int tx, int ty, Skill skill, Unit target, Damage damage) {
		if(skill.getEffect() == null)
			return;
		if(SRPG.VIEW)
			SRPG.getRenderer().getAnimationManager().startSerialAnimation(skill.getEffect().createEffect(screen, stage.coordToReal(cx, cy), stage.coordToReal(tx, ty), stage.SPAN, target, damage));
		else
			target.exertDamage(damage);
	}

	//	public void setOperator(UnitOperator operator) {
	//		this.operator = operator;
	//	}

	public int getPosX() { return cx; }
	public int getPosY() { return cy; }

	public Vector3 getRealCoord() {
		return realCoord.cpy();
	}

	public UnitOperator getOperater() {
		return operator;
	}

	public Status getStatus() {
		return status;
	}

	public boolean isStandby() {
		return status.isStandby();
	}

	public boolean isDead() {
		return status.isDead();
	}

	public void exertDamage(Damage dam) {
		status.damage(dam);
	}

	public void kill() {
		dispose();
	}

	@Override
	public void update(float delta) {

	}

	public void turnUpdate() {
		status.update(0);
	}

	@Override
	public void render(GL10 gl) {
		RenderUtil.setupBillboard(decal);
	}

	@Override
	public void dispose() {
		if(decal != null)
			SRPG.getRenderer().removeDecalFromBatch(decal);
		statusBar.dispose();
		super.dispose();
		//		mesh.dispose();
		//		texture.dispose();
	}

	@Override
	protected void initRender() {
		initGraph();
	}

	@Override
	public String toString() {
		return status.getName() + "(HP " + status.getCurrentHp() + ", MP " + status.getCurrentMp() + ")";// + "(" + super.toString() + ")";
	}

}
