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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Handler;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.parrot.arsdk.arcontroller.ARFrame;

import android.widget.ImageView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoDataContainer;
import org.catrobat.catroid.io.StorageHandler;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.badlogic.gdx.Gdx.gl;

import static org.catrobat.catroid.CatroidApplication.getAppContext;

public class JumpingSumoVideoLookData extends LookData {

	private static final String TAG = JumpingSumoVideoLookData.class.getSimpleName();

	private transient boolean firstStart = true;
	private transient int[] videoSize = {0, 0};
	private transient int[] defaultVideoTextureSize;
	private transient JumpingSumoDataContainer pixmapReceiver = JumpingSumoDataContainer.getInstance();
	private transient final Handler handler = new Handler(getAppContext().getMainLooper());
	private transient ARFrame frame;
	private transient Bitmap bmp;
	private transient ImageView view;
	public static transient boolean changedAsdf = true;
	public static transient Pixmap framePixmap = null;
	public static transient Texture texture = new Texture(ScreenValues.SCREEN_WIDTH, ScreenValues.SCREEN_HEIGHT,
			Pixmap.Format.RGBA8888);

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

		defaultVideoTextureSize = new int[] {50, 50};
		if (pixmap != null && pixmap != framePixmap) {
			pixmap.dispose();
			pixmap = framePixmap;
		}
		if (pixmap == null) {
			pixmap = new Pixmap(ScreenValues.SCREEN_HEIGHT, ScreenValues.SCREEN_WIDTH, Pixmap.Format.RGB888);
			pixmap.setColor(Color.RED);
			pixmap.fill();
			pixmap.setBlending(Pixmap.Blending.None);
		}
		//pixmap = pixmapReceiver.getFramePixmap();
		return pixmap;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {

		/*if (firstStart) {
			Pixmap pm = new Pixmap(ScreenValues.SCREEN_HEIGHT, ScreenValues.SCREEN_WIDTH, Pixmap.Format.RGBA8888);
			pm.setColor(Color.BLUE);
			pm.fill();
			texture = new Texture(pm);
			firstStart = false;
		}
		Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, texture.getTextureObjectHandle());
		batch.draw(texture, 0, 0);*/
		if (changedAsdf) {


		}
		/*byte[] data = pixmapReceiver.getFrame().getByteData();
		bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		view.setImageBitmap(bmp);*/
		/*synchronized (this) {
			pix = pixmapReceiver.getFramePixmap();
			if (pix == null) {
				Log.e(TAG, "pixmap:");
				return;
			}
			tex = new Texture(new PixmapTextureData(pix, pix.getFormat(), false, false, true));
			//tex = new Texture(pix);
			textureRegion.setTexture(tex);
		}*/
		/*
		bmp = pixmapReceiver.getBitmap();
		Log.d(TAG, pixmapReceiver.getBitmap().toString());
		synchronized (this) {
			tex = new Texture(bmp.getWidth(), bmp.getHeight(), Pixmap.Format.RGB888);
			Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, tex.getTextureObjectHandle());
			Log.d(TAG, tex.toString());
			GLUtils.texImage2D(GL20.GL_TEXTURE_2D, 0, bmp, 0);
			Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, 0);
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
			bmp.recycle();

			/*if(bmp!=null)
			{
				bmp.recycle();
				bmp=null;
			}*/
		//Gdx.gl20.glFlush();
	}

	//Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, textureRegion.getTexture().getTextureObjectHandle());
	//}
	//Log.d(TAG, "pix" + data.hashCode());
	//Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, textureRegion.getTexture().getTextureObjectHandle());
		/*
		batch.setColor(Color.RED);
		Log.d(TAG, "pixmap:" + pixm.getHeight() + ":" + pixm.hashCode());
		Texture tex = new Texture(new PixmapTextureData(pixm, pixm.getFormat(), false, false, true));
		Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, tex.getTextureObjectHandle());
		tex.draw(pixm, 30, 30);
		pixm.dispose();
		textureRegion = new TextureRegion(tex);
		Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, textureRegion.getTexture().getTextureObjectHandle());*/
	//Log.d(TAG, "pix" + pix.hashCode());

	//setPixmap(pixmap);
	//Texture tex = new Texture(pixmap);
	//textureRegion = new TextureRegion(tex);

		/*if (firstStart) {
			videoTexture = new GLBGVideoSprite();
			onSurfaceChanged();
			firstStart = false;
		}
		if (videoSize[0] != videoTexture.imageWidth || videoSize[1] != videoTexture.imageHeight) {
			onSurfaceChanged();
		}
		Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, textureRegion.getTexture().getTextureObjectHandle());
		videoTexture.onUpdateVideoTexture();
	}
	private void onSurfaceChanged() {
		videoSize[0] = videoTexture.imageWidth;
		videoSize[1] = videoTexture.imageHeight;
		videoTexture.onSurfaceChanged(videoSize[0], videoSize[1]);
		//setSize(1f, 1f * Gdx.graphics.getHeight() / Gdx.graphics.getWidth());
*/

	@Override
	public int getRequiredResources() {
		return Brick.JUMPING_SUMO;
	}
}