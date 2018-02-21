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
import android.util.Log;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.io.StorageHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JumpingSumoVideoLookData extends LookData {

	private static final String TAG = JumpingSumoVideoLookData.class.getSimpleName();

	private transient boolean firstStart = true;
	private transient int[] defaultVideoTextureSize;
	private transient Texture texture;
	public static transient ConcurrentLinkedQueue<Pixmap> videoPixmaps = new ConcurrentLinkedQueue<>();

	@Override
	public JumpingSumoVideoLookData clone() {
		String copiedFileName;
		try {
			copiedFileName = StorageHandler.copyFile(getAbsolutePath()).getName();
		} catch (IOException e) {
			Log.e(TAG, "Could not copy file: " + fileName + ", fallback to shallow clone.");
			copiedFileName = fileName;
		}
		JumpingSumoVideoLookData clone = new JumpingSumoVideoLookData();
		clone.setName(name);
		clone.setFileName(copiedFileName);
		return clone;
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

	public Rect getScaledDimensions(int width, int height) {
		float ratio = (float) ScreenValues.SCREEN_WIDTH / width;
		int scaledWidth = Math.round(width * ratio);
		int scaledHeight = Math.round(height * ratio);
		if (scaledHeight < ScreenValues.SCREEN_HEIGHT) {
			ratio = (float) ScreenValues.SCREEN_HEIGHT / height;
			scaledWidth = Math.round(width * ratio);
			scaledHeight = Math.round(height * ratio);
		}
		return new Rect(0, 0, scaledWidth, scaledHeight);
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
		if (firstStart) { // TODO: reset firstStart
			texture = new Texture(currentPixmap);
			firstStart = false;
		} else {
			texture.draw(currentPixmap, 0, 0);
		}
		Image image = new Image(texture);
		image.setX(-(ScreenValues.SCREEN_WIDTH / 2));
		image.setY(-(ScreenValues.SCREEN_HEIGHT / 2));

		Rect scaledDimensions = getScaledDimensions(texture.getWidth(), texture.getHeight());
		image.setHeight(scaledDimensions.height());
		image.setWidth(scaledDimensions.width());
		image.draw(batch, parentAlpha);
	}

	@Override
	public int getRequiredResources() {
		return Brick.JUMPING_SUMO;
	}
}