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

import java.io.FileNotFoundException;

import static com.badlogic.gdx.Gdx.gl;
import static org.catrobat.catroid.CatroidApplication.getAppContext;

public class JumpingSumoVideoLookData extends LookData {

	private static final String TAG = JumpingSumoVideoLookData.class.getSimpleName();

	private transient boolean firstStart = true;
	private transient int[] videoSize = { 0, 0 };
	private transient int[] defaultVideoTextureSize;
	private transient JumpingSumoDataContainer pixmapReceiver =  JumpingSumoDataContainer.getInstance();
	private final Handler handler = new Handler(getAppContext().getMainLooper());
	private ARFrame frame;
	private Bitmap bmp;
	private ImageView view;

	@Override
	public JumpingSumoVideoLookData clone() {
		JumpingSumoVideoLookData cloneVideoLookData = new JumpingSumoVideoLookData();

		cloneVideoLookData.name = this.name;
		cloneVideoLookData.fileName = this.fileName;
		String filePath = getPathToImageDirectory() + "/" + fileName;
		try {
			ProjectManager.getInstance().getFileChecksumContainer().incrementUsage(filePath);
		} catch (FileNotFoundException fileNotFoundexception) {
			Log.e(TAG, Log.getStackTraceString(fileNotFoundexception));
		}

		return cloneVideoLookData;
	}

	@Override
	public int[] getMeasure() {
		return defaultVideoTextureSize.clone();
	}

	@Override
	public Pixmap getPixmap() {
		// BUG: Height() should be 1280, but it is 1184, so we need an scaling factor of 1.081081
		int virtualScreenHeight = (int) Math.round(1.081081 * ScreenValues.SCREEN_HEIGHT);

		defaultVideoTextureSize = new int[] { virtualScreenHeight, ScreenValues.SCREEN_WIDTH };

		if (pixmap == null) {
			pixmap = new Pixmap(virtualScreenHeight, ScreenValues.SCREEN_WIDTH, Pixmap.Format.RGB888);
			pixmap.setColor(Color.BLUE);
			pixmap.fill();
			pixmap.setBlending(Pixmap.Blending.None);
		}
		//pixmap = pixmapReceiver.getFramePixmap();
		return pixmap;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
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
		super.draw(batch, parentAlpha);

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
*/	}

	@Override
	public int getRequiredResources() {
		return Brick.JUMPING_SUMO;
	}
}
