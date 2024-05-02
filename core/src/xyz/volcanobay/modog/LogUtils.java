package xyz.volcanobay.modog;

public class LogUtils {
    
    public static void logBytes(byte[] bytes) {
        String hexChars = "0123456789ABCDEF";
        StringBuilder hexString = new StringBuilder();
        int i = 0;
        for (byte b : bytes) {
            hexString.append(
                hexChars.charAt(((b >>> 4) & 0xf)) + "" + hexChars.charAt(((b) & 0xf)) + " "
            );
        
            if (i % 4 == 3)
                hexString.append("- ");
            i++;
        }
        System.out.println(hexString);
    }
    
}
