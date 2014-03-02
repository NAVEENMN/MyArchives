import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;

public class Dither
{
    public static void main(String[] args) throws Exception
    {
        /*
        Dither d = new Dither();
        BufferedImage old = ImageIO.read(new File("in.png"));
        BufferedImage bmp = new BufferedImage(144, 168, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bmp.createGraphics();
        g.drawImage(old, 0, 0, 144, 168, null);
        g.dispose();
        bmp = d.process(bmp);
        File out = new File("out.png");
        ImageIO.write(bmp, "png", out);
        */
        Dither d = new Dither();
        BufferedImage bmp = ImageIO.read(new File("in.png"));
        bmp = d.process(bmp);
        File out = new File("out.png");
        ImageIO.write(bmp, "png", out);
    }

    private long tone;

    private int decode(int pixel)
    {
        float r = ((pixel>>16) & 0xFF);
        float g = ((pixel>> 8) & 0xFF);
        float b = ((pixel    ) & 0xFF);
        return (int)((r+g+b)/3);
    }

    private int encode(int i)
    {
        return (0xFF<<24) | (i<<16) | (i<< 8) | i;
    }

    private int quantizeColor(int original)
    {
        int i=(int)Math.min(Math.max(original, 0),255);
        return ( i > tone ) ? 255 : 0;
    }

    private void ditherDirection(BufferedImage img, int y, int[] error, int[] nexterror, int direction) {
        int w = img.getWidth();
        int oldPixel, newPixel, quant_error;
        int start, end, x;

        for(x = 0; x < w; x++)
            nexterror[x] = 0;

        if(direction>0)
        {
            start=0;
            end=w;
        }
        else
        {
            start=w-1;
            end=-1;
        }

        for(x = start; x != end; x += direction) {
            oldPixel = decode(img.getRGB(x, y)) + error[x];
            newPixel = quantizeColor(oldPixel);
            img.setRGB(x, y, encode(newPixel));
            quant_error = oldPixel - newPixel;
                nexterror[x          ] += 5.0/16.0 * quant_error;
            if(x+direction>=0 && x+direction < w) {
                    error[x+direction] += 7.0/16.0 * quant_error;
                nexterror[x+direction] += 1.0/16.0 * quant_error;
            }
            if(x-direction>=0 && x-direction < w) {
                nexterror[x-direction] += 3.0/16.0 * quant_error;
            }
        }
    }


    public BufferedImage process(BufferedImage img) {
        int y, x;
        int h = img.getHeight();
        int w = img.getWidth();
        int direction = 1;
        int[] error = new int[w];
        int[] nexterror = new int[w];

        for(y = 0; y < w; y++)
        {
            error[y] = nexterror[y] = 0;
        }

        // Average
        for(y=0;y<h;++y)
        {
            for(x=0;x<w;++x)
            {
                tone += decode(img.getRGB(x,y));
            }
        }
        tone /= (w*h);


        for(y = 0; y < h; y++)
        {
            ditherDirection(img, y, error, nexterror, direction);

            direction = direction> 0 ? -1 : 1;
            int[] tmp = error;
            error = nexterror;
            nexterror = tmp;
        }

        return img;
    }
}
