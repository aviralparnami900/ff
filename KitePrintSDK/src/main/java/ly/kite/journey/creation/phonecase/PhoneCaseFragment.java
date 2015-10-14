/*****************************************************
 *
 * PhoneCaseFragment.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2015 Kite Tech Ltd. https://www.kite.ly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The software MAY ONLY be used with the Kite Tech Ltd platform and MAY NOT be modified
 * to be used with any competitor platforms. This means the software MAY NOT be modified 
 * to place orders with any competitors to Kite Tech Ltd, all orders MUST go through the
 * Kite Tech Ltd platform servers. 
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.journey.creation.phonecase;


///// Import(s) /////

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;

import java.net.URL;
import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.journey.creation.AEditImageFragment;
import ly.kite.journey.AKiteActivity;
import ly.kite.journey.AssetsAndQuantity;
import ly.kite.journey.ImageSource;
import ly.kite.catalogue.Asset;
import ly.kite.catalogue.AssetHelper;
import ly.kite.catalogue.Bleed;
import ly.kite.catalogue.Product;
import ly.kite.util.ImageAgent;


///// Class Declaration /////

/*****************************************************
 *
 * This activity allows the user to create a phone
 * case design using an image.
 *
 *****************************************************/
public class PhoneCaseFragment extends AEditImageFragment
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String      LOG_TAG                                   = "PhoneCaseFragment";

  private static final String      BUNDLE_KEY_IMAGE_ASSET                    = "imageAsset";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Asset  mImageAsset;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static PhoneCaseFragment newInstance( Product product )
    {
    PhoneCaseFragment fragment = new PhoneCaseFragment();

    Bundle arguments = new Bundle();
    arguments.putParcelable( BUNDLE_KEY_PRODUCT, product );

    fragment.setArguments( arguments );

    return ( fragment );
    }


  ////////// Constructor(s) //////////


  ////////// AEditImageFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the activity is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    // See if we can find a saved asset

    if ( savedInstanceState != null )
      {
      mImageAsset = savedInstanceState.getParcelable( BUNDLE_KEY_IMAGE_ASSET );
      }


    setHasOptionsMenu( true );
    }


  /*****************************************************
   *
   * Called the first time the options menu is created.
   *
   *****************************************************/
  @Override
  public void onCreateOptionsMenu( Menu menu, MenuInflater menuInflator )
    {
    // The add photo XML has menu options for all the image sources, but they might
    // not all be enabled. So after we've inflated it, we need to go through an remove
    // any source that isn't available.

    menuInflator.inflate( R.menu.add_photo, menu );

    MenuItem addPhotoItem = menu.findItem( R.id.add_photo_menu_item );

    if ( addPhotoItem != null )
      {
      SubMenu addPhotoSubMenu = addPhotoItem.getSubMenu();

      if ( addPhotoSubMenu != null )
        {
        for ( ImageSource imageSource : KiteSDK.getInstance( mKiteActivity ).getAvailableImageSources() )
          {
          imageSource.addMenuItem( addPhotoSubMenu );
          }
        }
      }
    }


  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = super.onCreateView( layoutInflator, container, savedInstanceState );


    mCancelButton.setVisibility( View.GONE );

    mConfirmButton.setVisibility( View.VISIBLE );
    mConfirmButton.setText( R.string.phone_case_proceed_button_text );


    return ( view );
    }


  /*****************************************************
   *
   * Called after the activity is created.
   *
   *****************************************************/
  @Override
  public void onActivityCreated( Bundle savedInstanceState )
    {
    super.onActivityCreated( savedInstanceState );


    // Try to restore any previous cropping
    if ( savedInstanceState != null )
      {
      mEditableImageContainerFrame.restoreState( savedInstanceState );
      }


    // Request the mask

    ImageAgent imageAgent = ImageAgent.getInstance( mKiteActivity );

    URL        maskURL      = mProduct.getMaskURL();
    Bleed      maskBleed    = mProduct.getMaskBleed();

    if ( maskURL != null )
      {
      mEditableImageContainerFrame.setMaskExtras( maskURL, maskBleed );

      imageAgent.requestImage( AKiteActivity.IMAGE_CLASS_STRING_PRODUCT_ITEM, maskURL, mEditableImageContainerFrame );
      }


    // If we haven't already got an image asset - look in the asset list

    if ( mImageAsset == null )
      {
      if ( mAssetsAndQuantityArrayList != null && mAssetsAndQuantityArrayList.size() > 0 )
        {
        mImageAsset = mAssetsAndQuantityArrayList.get( 0 ).getUneditedAsset();
        }
      }
    }


  /*****************************************************
   *
   * Called when an item in the options menu is selected.
   *
   *****************************************************/
  @Override
  public boolean onOptionsItemSelected( MenuItem item )
    {
    // See what menu item was selected

    int itemId = item.getItemId();

    if ( itemId == ImageSource.DEVICE.menuItemId() )
      {
      ///// Local device photo /////

      // Pick a single image from the local device
      ImageSource.DEVICE.onPick( this, true );

      return ( true );
      }
    else if ( itemId == ImageSource.INSTAGRAM.menuItemId() )
      {
      ///// Instagram photo /////

      // Pick a single image from instagram
      ImageSource.INSTAGRAM.onPick( this, true );

      return ( true );
      }


    return ( super.onOptionsItemSelected( item ) );
    }


  /*****************************************************
   *
   * Called with the result of an activity.
   *
   *****************************************************/
  @Override
  public void onActivityResult( int requestCode, int resultCode, Intent returnedIntent )
    {
    super.onActivityResult( requestCode, resultCode, returnedIntent );


    // Get assets for any images returned

    List<Asset> assetList = ImageSource.getAssetsFromResult( requestCode, resultCode, returnedIntent );

    if ( assetList != null && assetList.size() > 0 )
      {
      // Add the assets to the shared list
      for ( Asset asset : assetList )
        {
        mAssetsAndQuantityArrayList.add( 0, new AssetsAndQuantity( asset ) );
        }


      // Save the first asset and use it

      mImageAsset = mAssetsAndQuantityArrayList.get( 0 ).getUneditedAsset();

      useAssetForImage( mImageAsset, true );
      }
    }


  /*****************************************************
   *
   * Called when the fragment is top-most.
   *
   *****************************************************/
  @Override
  public void onTop()
    {
    super.onTop();

    if ( mProduct != null ) mKiteActivity.setTitle( mProduct.getName() );

    if ( mImageAsset != null ) useAssetForImage( mImageAsset, false );
    }


  /*****************************************************
   *
   * Called when the fragment is not top-most.
   *
   *****************************************************/
  @Override
  public void onNotTop()
    {
    super.onNotTop();

    if ( mEditableImageContainerFrame != null ) mEditableImageContainerFrame.clearImage();
    }


  /*****************************************************
   *
   * Called to save the instance state.
   *
   *****************************************************/
  @Override
  public void onSaveInstanceState( Bundle outState )
    {
    // A different asset may have been selected by the user, which is why we save the most
    // recent version.

    if ( mImageAsset != null )
      {
      outState.putParcelable( BUNDLE_KEY_IMAGE_ASSET, mImageAsset );
      }


    // Try and save the cropping

    if ( mEditableImageContainerFrame != null )
      {
      mEditableImageContainerFrame.saveState( outState );
      }

    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Uses the supplied asset for the photo.
   *
   *****************************************************/
  private void useAssetForImage( Asset asset, boolean imageIsNew )
    {
    if ( imageIsNew ) mEditableImageContainerFrame.clearState();

    mEditableImageContainerFrame.clearForNewImage( asset );

    // When we have the image - kick off the prompt text display cycle if this is the first time
    // we're loading an image.
    AssetHelper.requestImage( mKiteActivity, asset, new PromptTextTrigger( mEditableImageContainerFrame ) );
    }


  /*****************************************************
   *
   * Called when the Next button is clicked.
   *
   *****************************************************/
  @Override
  protected void onConfirm()
    {
    Asset editedImageAsset = getEditedImageAsset();

    if ( editedImageAsset == null ) return;


    // Call back to the activity
    if ( mKiteActivity instanceof ICallback )
      {
      ( (ICallback)mKiteActivity ).pcOnCreated( editedImageAsset );
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void pcOnCreated( Asset imageAsset );
    }

  }

