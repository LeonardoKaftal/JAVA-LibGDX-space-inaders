package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;



public class MyGdxGame extends ApplicationAdapter {
	private final int SCREEN_WIDTH = 900;
	private final int SCREEN_HEIGHT = 800;
	private float lastShotPlayerElapsedTime = 0;
	private float lastShootEnemyElapsedTime = 0;
	private float lastShotTime = 0;
	private SpriteBatch batch;
	private Texture background;
	private Texture playerTexture;
	private Texture playerBulletTexture;
	private Texture alien1Texture;
	private Texture alien2Texture;
	private Texture alien3Texture;
	private Texture enemyBulletTexture;
	private Rectangle player;
	private final Array<Rectangle> playerBullets = new Array<>();
	private final Array<Rectangle> enemyBullets = new Array<>();
	private final Array<Alien> aliensList = new Array<>();
	private final int[][] enemyMap = {
			{1, 1, 3, 1, 1, 1, 1, 1, 1, 1},
			{2, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{2, 2, 2, 3, 3, 3, 3, 2, 2, 2},
			{2, 2, 2, 3, 3, 3, 3, 2, 2, 2},
			{1, 1, 3, 1, 1, 1, 3, 1, 1, 1},
	};
	private boolean enemiesAreGoingRight = true;
	private boolean enemiesAreGoingLeft = false;


	public void spawnEnemy() {
		int alienWidth = 70;
		int alienHeight = 70;

		for (int i = 0; i < enemyMap.length; i++) {
			for (int j = 0; j < enemyMap[0].length; j++) {
				int alienXPosition = j * alienWidth;
				int alienYPosition = (SCREEN_WIDTH - alienHeight * 2) - i * alienHeight;
				switch (enemyMap[i][j]) {
					case 1:
						aliensList.add(new Alien(alienXPosition,alienYPosition,alienWidth,alienHeight,alien1Texture));
						break;
					case 2:
						aliensList.add(new Alien(alienXPosition, alienYPosition,alienWidth,alienHeight,alien2Texture));
						break;
					case 3:
						aliensList.add(new Alien(alienXPosition, alienYPosition,alienWidth,alienHeight,alien3Texture));
						break;
				}
			}
		}
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture(Gdx.files.internal("space.png"));
		playerTexture = new Texture(Gdx.files.internal("player.png"));
		playerBulletTexture = new Texture(Gdx.files.internal("bullet.png"));
		enemyBulletTexture = new Texture(Gdx.files.internal("enemyBullet.png"));
		alien1Texture = new Texture(Gdx.files.internal("enemy.png"));
		alien2Texture = new Texture(Gdx.files.internal("enemy2.png"));
		alien3Texture = new Texture(Gdx.files.internal("enemy3.png"));
		player = new Rectangle(SCREEN_WIDTH / 2f - 75 / 2f, 25,75,75);
		spawnEnemy();
	}

	public void movePlayer() {
		if (Gdx.input.isKeyPressed(Input.Keys.D)) player.x += 700 * Gdx.graphics.getDeltaTime();
		else if (Gdx.input.isKeyPressed(Input.Keys.A)) player.x -= 700 * Gdx.graphics.getDeltaTime();
		if (player.x > SCREEN_WIDTH - player.width) player.x = SCREEN_WIDTH - player.width;
		else if (player.x < 0) player.x = 0;
	}

	public void generateBullet() {
		Rectangle playerBullet = new Rectangle((player.x + player.width / 2) - 10 / 2f, player.y + player.height, 10, 20);
		playerBullets.add(playerBullet);
	}

	public void drawPLayerBullet() {
		playerBullets.forEach(playerBullet -> {
			batch.draw(playerBulletTexture,playerBullet.x,playerBullet.y,playerBullet.width,playerBullet.height);
			playerBullet.y += 400 * Gdx.graphics.getDeltaTime();
			if (playerBullet.y > SCREEN_HEIGHT) playerBullets.removeIndex(playerBullets.indexOf(playerBullet,true));
		});
	}

	public void drawAliens() {
		aliensList.forEach(alien -> {
			batch.draw(alien.getTexture(),alien.x,alien.y);
		});
	}

	public void moveAliens() {
		float alienMovement = 150 * Gdx.graphics.getDeltaTime();
		boolean hasHitWall = false;

		if (enemiesAreGoingRight) {
			for (Alien alien : aliensList) {
				alien.x += alienMovement;
			}
		}
		else if (enemiesAreGoingLeft) {
			for (Alien alien : aliensList) {
				alien.x -= alienMovement;
			}
		}
		for (Alien alien : aliensList) {
			if (alien.x < 0 || alien.x + alien.getWidth() > SCREEN_WIDTH) {
				hasHitWall = true;
				break;
			}
		}
		if (hasHitWall) {
			for (Alien alien : aliensList) {
				alien.y -= SCREEN_HEIGHT / 25f;
			}

			if (enemiesAreGoingRight) {
				enemiesAreGoingRight = false;
				enemiesAreGoingLeft = true;
			}
			else {
				enemiesAreGoingRight = true;
				enemiesAreGoingLeft = false;
			}
		}
	}

	public void generateEnemyBullet() {
		final int randomIndex = MathUtils.random(0,aliensList.size - 1);
		final int alienX = aliensList.get(randomIndex).x;
		final int alienY = aliensList.get(randomIndex).y;
		final int alienWidth = aliensList.get(randomIndex).getWidth();

		Rectangle enemyBullet = new Rectangle((alienX + alienWidth / 2f) - 10 / 2f, alienY, 10, 20);
		enemyBullets.add(enemyBullet);
	}

	public void drawEnemyBullet() {
		for (Rectangle enemyBullet : enemyBullets) {
			enemyBullet.y -= (500 * Gdx.graphics.getDeltaTime());
			batch.draw(enemyBulletTexture,enemyBullet.x,enemyBullet.y,enemyBullet.width,enemyBullet.height);
			if (enemyBullet.y < 0) enemyBullets.removeValue(enemyBullet, false);
		}
	}

	public void checkBulletCollision() {
		for (Rectangle playerBullet: playerBullets) {
			for (Alien alien: aliensList) {
				Rectangle alienRec = new Rectangle(alien.x,alien.y,alien.getWidth(),alien.getHeight());
				if (playerBullet.overlaps(alienRec)) {
					aliensList.removeValue(alien,false);
					playerBullets.removeValue(playerBullet, false);
				}
			}
		}

	}

	public void checkGameOver() {

		if (aliensList.size == 0) {
			System.out.println("YOU WON");
			Gdx.app.exit();
		}

		for (Rectangle enemyBullet: enemyBullets) {
			if (enemyBullet.overlaps(player)) Gdx.app.exit();
		}

		for (Alien alien: aliensList) {
			Rectangle alienRec = new Rectangle(alien.x,alien.y,alien.getWidth(),alien.getHeight());
			if (alienRec.overlaps(player)) Gdx.app.exit();
		}


	}

	@Override
	public void render() {
		checkGameOver();
		lastShotPlayerElapsedTime += Gdx.graphics.getDeltaTime();
		lastShootEnemyElapsedTime += Gdx.graphics.getDeltaTime();

		if (lastShotPlayerElapsedTime - lastShotTime >= 0.4) {
			if (Gdx.input.isKeyPressed(Input.Keys.W)) {
				generateBullet();
				lastShotTime = lastShotPlayerElapsedTime;
			}
		}

		if (lastShootEnemyElapsedTime > 1) {
			generateEnemyBullet();
			lastShootEnemyElapsedTime = 0;
		}

		checkBulletCollision();
		movePlayer();
		moveAliens();


		batch.begin();
		batch.draw(background, 0,0, SCREEN_WIDTH,SCREEN_HEIGHT);
		batch.draw(playerTexture,player.x,player.y,player.width,player.height);
		drawAliens();
		drawPLayerBullet();
		drawEnemyBullet();
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		playerTexture.dispose();
		playerBulletTexture.dispose();
		enemyBulletTexture.dispose();
	}
}
