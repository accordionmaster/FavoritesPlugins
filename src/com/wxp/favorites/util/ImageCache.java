package com.wxp.favorites.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * 在Favorites插件中，入股哦你需要载入你自己的图像，实例化一个与下面所示类似的类以缓存载入的图像。
 * 该类通过惰性载入图像遵循了Eclipse的原则。它当图像被请求时才被载入图像，而不是当插件启动时或视图第一次打开时
 * 就立即载入所有图像。插件的stop()方法将会被修改以调用实例的dispose()方法，以使图像在插件被关闭时可以得到清理。
 * @author Dell
 *
 */
public class ImageCache
{
   private final Map<ImageDescriptor, Image> imageMap =
         new HashMap<ImageDescriptor, Image>();

   public Image getImage(ImageDescriptor imageDescriptor) {
      if (imageDescriptor == null)
         return null;
      Image image = (Image) imageMap.get(imageDescriptor);
      if (image == null) {
         image = imageDescriptor.createImage();
         imageMap.put(imageDescriptor, image);
      }
      return image;
   }

   public void dispose() {
      Iterator<Image> iter = imageMap.values().iterator();
      while (iter.hasNext())
         iter.next().dispose();
      imageMap.clear();
   }
}
