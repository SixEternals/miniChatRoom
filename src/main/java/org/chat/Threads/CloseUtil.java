package org.chat.Threads;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtil {
 public static void CloseAll(Closeable... closeable){
  for(Closeable c:closeable){
   if (c != null) {
    try {
     c.close();
    } catch (IOException e) {
     e.printStackTrace();
    }
   }
  }
 }
}
