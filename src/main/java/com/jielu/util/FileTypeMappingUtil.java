package com.jielu.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileTypeMappingUtil {

    private static FileType getFileType(byte[]inputStreamByte) throws IOException {
        byte[]copy=new byte[20];
        System.arraycopy(inputStreamByte,0,copy,0,20);
        String hex=bytesToMagicFileTypeHexString(copy);
        return FileType.getFileType(hex);

    }
        /**
         * @description
         * @param
         * @return 16
         */
    private static String bytesToMagicFileTypeHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }




    public static void main(String[] args) throws IOException {
        FileInputStream fileReadernew=new FileInputStream(new File("D:/mmexport1656057743660.mp5"));
        byte []bs=new byte[fileReadernew.available()];
        fileReadernew.read(bs);
        System.out.println(bs.length);
        System.out.println(getFileType(bs));
    }
    public  enum FileType {
        /**
         * UNKNOWN TYPE
         */
        UN_KNOWN("-1"),
        /**
         * JEPG.
         */
        JPEG("FFD8FF"),

        /**
         * PNG.
         */
        PNG("89504E47"),

        /**
         * GIF.
         */
        GIF("47494638"),

        /**
         * TIFF.
         */
        TIFF("49492A00"),

        TXT("6C657420"),

        /**
         * Windows Bitmap.
         */
        BMP("424D"),

        /**
         * CAD.
         */
        DWG("41433130"),

        /**
         * Adobe Photoshop.
         */
        PSD("38425053"),

        /**
         * Rich Text Format.
         */
        RTF("7B5C727466"),

        /**
         * XML.
         */
        XML("3C3F786D6C"),

        /**
         * HTML.
         */
        HTML("68746D6C3E"),

        /**
         * Email [thorough only].
         */
        EML("44656C69766572792D646174653A"),

        /**
         * Outlook Express.
         */
        DBX("CFAD12FEC5FD746F"),

        /**
         * Outlook (pst).
         */
        PST("2142444E"),

        /**
         * MS Word/Excel.
         */
        XLS_DOC("D0CF11E0"),

        /**
         * MS Access.
         */
        MDB("5374616E64617264204A"),

        /**
         * WordPerfect.
         */
        WPD("FF575043"),

        /**
         * Postscript.
         */
        EPS("252150532D41646F6265"),

        /**
         * Adobe Acrobat.
         */
        PDF("255044462D312E"),

        /**
         * Quicken.
         */
        QDF("AC9EBD8F"),

        /**
         * Windows Password.
         */
        PWL("E3828596"),

        /**
         * ZIP Archive.
         */
        ZIP("504B0304"),

        /**
         * RAR Archive.
         */
        RAR("52617221"),

        /**
         * Wave.
         */
        WAV("57415645"),

        /**
         * AVI.
         */
        AVI("41564920"),

        /**
         * Real Audio.
         */
        RAM("2E7261FD"),

        /**
         * Real Media.
         */
        RM("2E524D46"),

        /**
         * MPEG (mpg).
         */
        MPG("000001BA"),

        /**
         * Quicktime.
         */
        MOV("6D6F6F76"),

        /**
         * Windows Media.
         */
        ASF("3026B2758E66CF11"),

        GZ("1F8B08"),
        /**
         * MIDI.
         */
        MID("4D546864"),

        MP4("000000206674797");

        private String value = "";


        /**
         * Constructor.
         * @param value
         */
        private FileType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static FileType getFileType(String magicHexOfFileType){
            System.out.println(magicHexOfFileType);
            for(FileType fileType:FileType.values()){
                if(magicHexOfFileType.startsWith(fileType.getValue())){
                    return fileType;
                }
            }
            return UN_KNOWN;
        }
    }
}
