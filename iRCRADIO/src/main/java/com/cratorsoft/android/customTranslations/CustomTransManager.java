package com.cratorsoft.android.customTranslations;

import com.earthflare.android.ircradio.Globo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by j on 4/6/2017.
 */

public class CustomTransManager {

    public static void moveAllLookupsToPublicStorage(){


        try {

            File externalFolder = new File(Globo.ctx.getExternalFilesDir(null) + "/lookup");
            externalFolder.mkdir();

            InputStream stream = null;
            OutputStream output = null;

            for (String fileName : Globo.ctx.getAssets().list("lookup")) {

                //test external file exists
                File outputfile = new File(externalFolder + "/" + fileName);


                if (!outputfile.exists()) {

                    stream = Globo.ctx.getAssets().open("lookup/" + fileName);
                    output = new BufferedOutputStream(new FileOutputStream(outputfile));

                    byte data[] = new byte[1024];
                    int count;

                    while ((count = stream.read(data)) != -1) {
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    stream.close();

                    stream = null;
                    output = null;


                }
            }


        }catch(Exception e){
            e.printStackTrace();
        }


    }

}
