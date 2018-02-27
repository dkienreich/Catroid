/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.common;

import android.graphics.Rect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.stage.StageActivity;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JumpingSumoVideoLookData extends LookData {

	private static final String TAG = JumpingSumoVideoLookData.class.getSimpleName();

	private transient int[] defaultVideoTextureSize;
	private transient Texture texture;
	private transient Rect dimensions;
	public static transient ConcurrentLinkedQueue<Pixmap> videoPixmaps = new ConcurrentLinkedQueue<>();

	public JumpingSumoVideoLookData(String name, File file) {
		super(name, file);
	}

	@Override
	public JumpingSumoVideoLookData clone() {
		try {
			return new JumpingSumoVideoLookData(name, StorageOperations.duplicateFile(file));
		} catch (IOException e) {
			throw new RuntimeException(TAG + ": Could not copy file: " + file.getAbsolutePath());
		}
	}

	@Override
	public int[] getMeasure() {
		return defaultVideoTextureSize.clone();
	}

	@Override
	public Pixmap getPixmap() {
		if (pixmap == null) {
			pixmap = new Pixmap(0, 0, Pixmap.Format.RGB888);
			pixmap.setBlending(Pixmap.Blending.None);
		}
		return pixmap;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {

		if (videoPixmaps.size() == 0) {
			return;
		}
		while (videoPixmaps.size() > 1) {
			videoPixmaps.poll().dispose();
		}

		Pixmap currentPixmap = videoPixmaps.peek();
		if (!StageActivity.stageListener.firstFrameDrawn) {
			if (texture != null) {
				texture.dispose();
			}
			texture = new Texture(currentPixmap);
			dimensions = getFullscreenDimensions(texture.getWidth(), texture.getHeight());
		} else {
			texture.draw(currentPixmap, 0, 0);
		}

		Color color = Color.WHITE;
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		batch.draw(texture, dimensions.left, dimensions.top, dimensions.width(), dimensions.height());
	}

	private Rect getFullscreenDimensions(int width, int height) {
		float ratio = (float) ScreenValues.SCREEN_WIDTH / width;
		int scaledWidth = Math.round(width * ratio);
		int scaledHeight = Math.round(height * ratio);
		int minXCoordinate = -(ScreenValues.SCREEN_WIDTH / 2);
		int minYCoordinate = -(ScreenValues.SCREEN_HEIGHT / 2);
		if (scaledHeight < ScreenValues.SCREEN_HEIGHT) {
			ratio = (float) ScreenValues.SCREEN_HEIGHT / height;
			scaledWidth = Math.round(width * ratio);
			scaledHeight = Math.round(height * ratio);
		}
		return new Rect(minXCoordinate, minYCoordinate, minXCoordinate + scaledWidth,
				minYCoordinate + scaledHeight);
	}

	@Override
	public int getRequiredResources() {
		return Brick.JUMPING_SUMO;
	}
}