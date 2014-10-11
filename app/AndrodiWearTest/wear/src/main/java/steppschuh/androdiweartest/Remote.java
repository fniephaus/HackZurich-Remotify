package steppschuh.androdiweartest;

import android.graphics.drawable.Drawable;

public class Remote {

	private int id;
	private String title;
	private Drawable background;
	private Drawable backgroundBlur;
	private Drawable icon;

	public Remote() {
	}

	public Remote(int id, String title, Drawable background) {
		this.id = id;
		this.title = title;
		this.background = background;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Drawable getBackground() {
		return background;
	}

	public void setBackground(Drawable background) {
		this.background = background;
	}

	public Drawable getBackgroundBlur() {
		return backgroundBlur;
	}

	public void setBackgroundBlur(Drawable backgroundBlur) {
		this.backgroundBlur = backgroundBlur;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
}
