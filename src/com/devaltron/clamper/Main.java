package com.devaltron.clamper;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Scanner sc = new Scanner(System.in);
		
		JFileChooser fc = new JFileChooser();
		fc.showOpenDialog(null);
		
		File file = fc.getSelectedFile();
		if(file != null)
		{
			BufferedImage source = ImageIO.read(file);
			WritableRaster sourceRaster = source.getRaster();
			
			System.out.println("Grid Width : ");
			int gridWidth = sc.nextInt();
			System.out.println("Grid Height : ");
			int gridHeight = sc.nextInt();
			System.out.println("Columns : ");
			int columns = sc.nextInt();
			System.out.println("Row : ");
			int rows = sc.nextInt();
			System.out.println("Clamp : ");
			int clamp = sc.nextInt();
			sc.close();
			
			int width = (gridWidth + clamp * 2);
			int height = (gridHeight + clamp * 2);
			
			BufferedImage output = new BufferedImage(columns * width + clamp, rows * height + clamp, BufferedImage.TYPE_4BYTE_ABGR);
			WritableRaster outputRaster = output.getRaster();
			
			for(int x = 0; x < columns; x++)
			{
				for(int y = 0; y < rows; y++)
				{
					int pX = x * width;
					int pY = y * height;
					
					int[] whole = sourceRaster.getPixels(x * gridWidth, y * gridHeight, gridWidth, gridHeight, (int[]) null);
					outputRaster.setPixels(pX + clamp, pY + clamp, gridWidth, gridHeight, whole);
					
					int[] top = sourceRaster.getPixels(x * gridWidth, y * gridHeight, gridWidth, 1, (int[]) null);
					int[] bottom = sourceRaster.getPixels(x * gridWidth, y * gridHeight + gridHeight - 1, gridWidth, 1, (int[]) null);
					
					int[] left = sourceRaster.getPixels(x * gridWidth, y * gridHeight, 1, gridHeight, (int[]) null);
					int[] right = sourceRaster.getPixels(x * gridWidth + gridWidth - 1, y * gridHeight, 1, gridHeight, (int[]) null);
					
					int[] cornerTopLeft = sourceRaster.getPixel(x * gridWidth, y * gridHeight, (int[]) null);
					int[] cornerTopRight = sourceRaster.getPixel(x * gridWidth + gridWidth - 1, y * gridHeight, (int[]) null);
					int[] cornerBottomRight = sourceRaster.getPixel(x * gridWidth + gridWidth - 1, y * gridHeight + gridHeight - 1, (int[]) null);
					int[] cornerBottomLeft = sourceRaster.getPixel(x * gridWidth, y * gridHeight + gridHeight - 1, (int[]) null);
					
					for(int i = 0; i < clamp; i++)
					{
						outputRaster.setPixels(pX + clamp, pY + i, gridWidth, 1, top);
						outputRaster.setPixels(pX + clamp, pY + gridHeight + clamp + i, gridWidth, 1, bottom);
						
						outputRaster.setPixels(pX + i, pY + clamp, 1, gridHeight, left);
						outputRaster.setPixels(pX + gridWidth + clamp + i, pY + clamp, 1, gridHeight, right);
						
						for(int j = 0; j < clamp; j++)
						{
							outputRaster.setPixel(pX + i, pY + j, cornerTopLeft);
							outputRaster.setPixel(pX + clamp + gridWidth + i, pY + j, cornerTopRight);
							outputRaster.setPixel(pX + clamp + gridWidth + i, pY + clamp + gridHeight + j, cornerBottomRight);
							outputRaster.setPixel(pX + i, pY + clamp + gridHeight + j, cornerBottomLeft);
						}
					}
				}
			}
			
			ImageIO.write(output, "png", new File(file.getParentFile(), "output.png"));
		}
	}
}
