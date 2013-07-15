package eu.trentorise.smartcampus.osm.android.views.overlay;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import eu.trentorise.smartcampus.osm.android.DefaultResourceProxyImpl;
import eu.trentorise.smartcampus.osm.android.ResourceProxy;

public class ItemizedOverlayControlView extends LinearLayout {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected ImageButton mPreviousButton;
	protected ImageButton mNextButton;
	protected ImageButton mCenterToButton;
	protected ImageButton mNavToButton;

	protected ItemizedOverlayControlViewListener mLis;

	// ===========================================================
	// Constructors
	// ===========================================================

	public ItemizedOverlayControlView(final Context context,
			final AttributeSet attrs) {
		this(context, attrs, new DefaultResourceProxyImpl(context));
	}

	public ItemizedOverlayControlView(final Context context,
			final AttributeSet attrs, final ResourceProxy pResourceProxy) {
		super(context, attrs);

		this.mPreviousButton = new ImageButton(context);
		this.mPreviousButton
				.setImageBitmap(pResourceProxy.getBitmap(ResourceProxy.bitmap.previous));

		this.mNextButton = new ImageButton(context);
		this.mNextButton.setImageBitmap(pResourceProxy.getBitmap(ResourceProxy.bitmap.next));

		this.mCenterToButton = new ImageButton(context);
		this.mCenterToButton.setImageBitmap(pResourceProxy.getBitmap(ResourceProxy.bitmap.center));

		this.mNavToButton = new ImageButton(context);
		this.mNavToButton
				.setImageBitmap(pResourceProxy.getBitmap(ResourceProxy.bitmap.navto_small));

		this.addView(mPreviousButton, new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.addView(mCenterToButton, new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.addView(mNavToButton, new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		this.addView(mNextButton, new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		initViewListeners();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setItemizedOverlayControlViewListener(final ItemizedOverlayControlViewListener lis) {
		this.mLis = lis;
	}

	public void setNextEnabled(final boolean pEnabled) {
		this.mNextButton.setEnabled(pEnabled);
	}

	public void setPreviousEnabled(final boolean pEnabled) {
		this.mPreviousButton.setEnabled(pEnabled);
	}

	public void setNavToVisible(final int pVisibility) {
		this.mNavToButton.setVisibility(pVisibility);
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	private void initViewListeners() {
		this.mNextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (ItemizedOverlayControlView.this.mLis != null)
					ItemizedOverlayControlView.this.mLis.onNext();
			}
		});

		this.mPreviousButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (ItemizedOverlayControlView.this.mLis != null)
					ItemizedOverlayControlView.this.mLis.onPrevious();
			}
		});

		this.mCenterToButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (ItemizedOverlayControlView.this.mLis != null)
					ItemizedOverlayControlView.this.mLis.onCenter();
			}
		});

		this.mNavToButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (ItemizedOverlayControlView.this.mLis != null)
					ItemizedOverlayControlView.this.mLis.onNavTo();
			}
		});
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public interface ItemizedOverlayControlViewListener {
		public void onPrevious();

		public void onNext();

		public void onCenter();

		public void onNavTo();
	}
}