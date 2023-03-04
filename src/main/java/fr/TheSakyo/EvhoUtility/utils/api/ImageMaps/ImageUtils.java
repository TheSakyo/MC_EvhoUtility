package fr.TheSakyo.EvhoUtility.utils.api.ImageMaps;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public abstract class ImageUtils {

  /****************************************************************************************/
  /* MÉTHODE POUR REDIMENSIONNER "L'IMAGEMAP" (UTILE POUR L'ADAPTER À L'ITEM DE LA MAP)  */
  /***************************************************************************************/

  public static BufferedImage scale(BufferedImage b, int width, int height) {
   
	if((b.getWidth() == width) && (b.getHeight() == height)) { return b; }
	
    AffineTransform a = AffineTransform.getScaleInstance((double) width / b.getWidth(), (double) height / b.getHeight());
    AffineTransformOp o = new AffineTransformOp(a, 2);
    
    return o.filter(b, new BufferedImage(width, height, b.getType()));
  }

  /****************************************************************************************/
  /* MÉTHODE POUR REDIMENSIONNER "L'IMAGEMAP" (UTILE POUR L'ADAPTER À L'ITEM DE LA MAP)  */
  /***************************************************************************************/
  
}
