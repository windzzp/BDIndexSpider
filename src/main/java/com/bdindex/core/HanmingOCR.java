package com.bdindex.core;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

//本类用于百度指数图片的数字识别
//主要思路是切割每个字符，并提取相应字符的指纹，与预定义的指纹通过汉明距离算法进行比对
//汉明距离可参考，http://www.ruanyifeng.com/blog/2011/07/principle_of_similar_image_search.html
//切割字符可参考，https://spaces.ac.cn/archives/3823
public class HanmingOCR {
	
	private static String n0FP = "000000000000000000000000111111111111111111111111000000000000000000000000000000000000000000000000111111111111111111111111000000000000000000000000000000000000000000000011111111111111111111111111110000000000000000000000000000000000000000000011111111111111111111111111110000000000000000000000000000000000000000001111110000000000000000000011111100000000000000000000000000000000000000001111110000000000000000000011111100000000000000000000000000000000000000111111000000000000000000000000111111000000000000000000000000000000000000111111000000000000000000000000111111000000000000000000000000000000000011111100000000000000000000000000001111110000000000000000000000000000000011111100000000000000000000000000001111110000000000000000000000000000000011110000000000000000000000000000000011110000000000000000000000000000000011110000000000000000000000000000000011110000000000000000000000000000000011110000000000000000000000000000000011110000000000000000000000000000000011110000000000000000000000000000000011110000000000000000000000000000000011111100000000000000000000000000001111110000000000000000000000000000000011111100000000000000000000000000001111110000000000000000000000000000000000111111000000000000000000000000111111000000000000000000000000000000000000111111000000000000000000000000111111000000000000000000000000000000000000001111110000000000000000000011111100000000000000000000000000000000000000001111110000000000000000000011111100000000000000000000000000000000000000000011111111111111111111111111110000000000000000000000000000000000000000000011111111111111111111111111110000000000000000000000000000000000000000000000111111111111111111111111000000000000000000000000000000000000000000000000111111111111111111111111000000000000000000000000";
	private static String n1FP = "000000000000000000000000111100000000000000000000000011110000000000000000000000000000000000000000111100000000000000000000000011110000000000000000000000000000000000000011111100000000000000000000000011110000000000000000000000000000000000000011111100000000000000000000000011110000000000000000000000000000000000001111110000000000000000000000000011110000000000000000000000000000000000001111110000000000000000000000000011110000000000000000000000000000000000111111110000000000000000000000001111110000000000000000000000000000000000111111110000000000000000000000001111110000000000000000000000000000000011111111111111111111111111111111111111110000000000000000000000000000000011111111111111111111111111111111111111110000000000000000000000000000000011111111111111111111111111111111111111110000000000000000000000000000000011111111111111111111111111111111111111110000000000000000000000000000000000000000000000000000000000000000001111110000000000000000000000000000000000000000000000000000000000000000001111110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000";
	private static String n2FP = "000000000000000000001111111100000000000000001111111111110000000000000000000000000000000000001111111100000000000000001111111111110000000000000000000000000000000000111111111100000000000000111111111111110000000000000000000000000000000000111111111100000000000000111111111111110000000000000000000000000000000011111100000000000000000011111100001111110000000000000000000000000000000011111100000000000000000011111100001111110000000000000000000000000000000011110000000000000000001111110000000011110000000000000000000000000000000011110000000000000000001111110000000011110000000000000000000000000000000011110000000000000000111111000000000011110000000000000000000000000000000011110000000000000000111111000000000011110000000000000000000000000000000011110000000000000011111100000000000011110000000000000000000000000000000011110000000000000011111100000000000011110000000000000000000000000000000011110000000000001111110000000000000011110000000000000000000000000000000011110000000000001111110000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000111111000000000000000011110000000000000000000000000000000011110000000000111111000000000000000011110000000000000000000000000000000000001111111111111100000000000000000011110000000000000000000000000000000000001111111111111100000000000000000011110000000000000000000000000000000000001111111111110000000000000000000011110000000000000000000000000000000000001111111111110000000000000000000011110000000000000000";
	private static String n3FP = "000000000000000000001111111100000000000000001111111100000000000000000000000000000000000000001111111100000000000000001111111100000000000000000000000000000000000000111111111100000000000000001111111111000000000000000000000000000000000000111111111100000000000000001111111111000000000000000000000000000000000011111100000000000000000000000000001111110000000000000000000000000000000011111100000000000000000000000000001111110000000000000000000000000000000011110000000000000000000000000000000011110000000000000000000000000000000011110000000000000000000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011111100000000111111110000000000001111110000000000000000000000000000000011111100000000111111110000000000001111110000000000000000000000000000000000111111111111111111111111111111111111000000000000000000000000000000000000111111111111111111111111111111111111000000000000000000000000000000000000001111111111110000111111111111111100000000000000000000000000000000000000001111111111110000111111111111111100000000000000000000";
	private static String n4FP = "000000000000000000000000000000001111111111110000000000000000000000000000000000000000000000000000000000001111111111110000000000000000000000000000000000000000000000000000000000111111111111110000000000000000000000000000000000000000000000000000000000111111111111110000000000000000000000000000000000000000000000000000000011111100001111110000000000000000000000000000000000000000000000000000000011111100001111110000000000000000000000000000000000000000000000000000001111110000000011110000000000000000000000000000000000000000000000000000001111110000000011110000000000000000000000000000000000000000000000000000111111000000000011110000000000000000000000000000000000000000000000000000111111000000000011110000000000000000000000000000000000000000000000000011111100000000000011110000000000000000000000000000000000000000000000000011111100000000000011110000000000000000000000000000000000000000000000001111110000000000000011110000000000000000000000000000000000000000000000001111110000000000000011110000000000000000000000000000000000000000000000111111110000000000001111111100000000000000000000000000000000000000000000111111110000000000001111111100000000000000000000000000000000000000000011111111111111111111111111111111111111110000000000000000000000000000000011111111111111111111111111111111111111110000000000000000000000000000000011111111111111111111111111111111111111110000000000000000000000000000000011111111111111111111111111111111111111110000000000000000000000000000000000000000000000000000001111111100000000000000000000000000000000000000000000000000000000000000001111111100000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000";
	private static String n5FP = "000000000000000011111111111111111111000000000000111100000000000000000000000000000000000011111111111111111111000000000000111100000000000000000000000000000000000011111111111111111111000000000000111111000000000000000000000000000000000011111111111111111111000000000000111111000000000000000000000000000000000011111100000000111111000000000000001111110000000000000000000000000000000011111100000000111111000000000000001111110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111110000000000001111110000000000000000000000000000000011110000000000001111110000000000001111110000000000000000000000000000000011110000000000000011111111111111111111000000000000000000000000000000000011110000000000000011111111111111111111000000000000000000000000000000000011110000000000000000111111111111111100000000000000000000000000000000000011110000000000000000111111111111111100000000000000000000";
	private static String n6FP = "000000000000000000000000111111111111111111111111111100000000000000000000000000000000000000000000111111111111111111111111111100000000000000000000000000000000000000000011111111111111111111111111111111000000000000000000000000000000000000000011111111111111111111111111111111000000000000000000000000000000000000001111110000111111110000000000001111110000000000000000000000000000000000001111110000111111110000000000001111110000000000000000000000000000000000111111000000001111000000000000000011110000000000000000000000000000000000111111000000001111000000000000000011110000000000000000000000000000000011111100000000001111000000000000000011110000000000000000000000000000000011111100000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111110000000000001111110000000000000000000000000000000011110000000000001111110000000000001111110000000000000000000000000000000000000000000000000011111111111111111111000000000000000000000000000000000000000000000000000011111111111111111111000000000000000000000000000000000000000000000000000000111111111111111100000000000000000000000000000000000000000000000000000000111111111111111100000000000000000000";
	private static String n7FP = "000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000000000000000000000000000000000000000000000000000011110000000000000000000011111111111111110000000000000000000000000000000011110000000000000000000011111111111111110000000000000000000000000000000011110000000000000000001111111111111111110000000000000000000000000000000011110000000000000000001111111111111111110000000000000000000000000000000011110000000011111111111111000000000000000000000000000000000000000000000011110000000011111111111111000000000000000000000000000000000000000000000011111100001111111111111100000000000000000000000000000000000000000000000011111100001111111111111100000000000000000000000000000000000000000000000011111111111111000000000000000000000000000000000000000000000000000000000011111111111111000000000000000000000000000000000000000000000000000000000011111111111100000000000000000000000000000000000000000000000000000000000011111111111100000000000000000000000000000000000000000000";
	private static String n8FP = "000000000000000000001111111111110000111111111111111100000000000000000000000000000000000000001111111111110000111111111111111100000000000000000000000000000000000000111111111111111111111111111111111111000000000000000000000000000000000000111111111111111111111111111111111111000000000000000000000000000000000011111100000000111111110000000000001111110000000000000000000000000000000011111100000000111111110000000000001111110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011111100000000111111110000000000001111110000000000000000000000000000000011111100000000111111110000000000001111110000000000000000000000000000000000111111111111111111111111111111111111000000000000000000000000000000000000111111111111111111111111111111111111000000000000000000000000000000000000001111111111110000111111111111111100000000000000000000000000000000000000001111111111110000111111111111111100000000000000000000";
	private static String n9FP = "000000000000000000001111111111110000000000000000000000000000000000000000000000000000000000001111111111110000000000000000000000000000000000000000000000000000000000111111111111111100000000000000000000000000000000000000000000000000000000111111111111111100000000000000000000000000000000000000000000000000000011111100000000111111000000000000000011110000000000000000000000000000000011111100000000111111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000000011110000000000000000000000000000000011110000000000001111000000000000001111110000000000000000000000000000000011110000000000001111000000000000001111110000000000000000000000000000000011110000000000001111000000000000111111000000000000000000000000000000000011110000000000001111000000000000111111000000000000000000000000000000000011111100000000111111110000000011111100000000000000000000000000000000000011111100000000111111110000000011111100000000000000000000000000000000000000111111111111111111111111111111110000000000000000000000000000000000000000111111111111111111111111111111110000000000000000000000000000000000000000001111111111111111111111111111000000000000000000000000000000000000000000001111111111111111111111111111000000000000000000000000";
	private static String[] fps = {n0FP,n1FP,n2FP,n3FP,n4FP,n5FP,n6FP,n7FP,n8FP,n9FP};
	
	
	private static String getFingerPrint(BufferedImage image) {
		String fingerprint = "";
		for (int j = 0; j < image.getWidth(); j++) {
			for (int k = 0; k < image.getHeight(); k++) {
				fingerprint += new Color(image.getRGB(j, k)).getRed()/255;
			}
		}
		return fingerprint;
	}
	
	//字符切割
	//输入：经过二值化的百度指数图片，用每列像素值相加是0表示全黑色，不为0表示有白色区域
	private static ArrayList<BufferedImage> clipBDIndexImage(BufferedImage image) {
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		
		int[] indices = getWidthForNumber(image, 0);
		while (indices != null) {
			int numberWidth = indices[1] - indices[0] + 1;
			if (numberWidth > 10) {//宽度太小的是逗号，不需要保存该数据
				BufferedImage subImage = image.getSubimage(indices[0], 0, numberWidth, image.getHeight());
				images.add(subImage);
			}
			indices = getWidthForNumber(image, indices[1]+1);
		}
		return images;
	}
	
	private static int[] getWidthForNumber(BufferedImage image, int beginColumn) {
		if (beginColumn >= image.getWidth() - 1) {
			return null;
		}
		// 找数字的开始位置
		int beginCol = 0;
		Boolean isBeginExists = false;
		for (int i = beginColumn + 1; i < image.getWidth(); i++) {
			// 第i列
			int columnRedSum = 0;
			for (int j = 0; j < image.getHeight(); j++) {
				columnRedSum += new Color(image.getRGB(i, j)).getRed();
			}
			if (columnRedSum != 0) {
				beginCol = i;
				isBeginExists = true;
				break;
			}
		}
		if (!isBeginExists) {
			return null;
		}
		// 找数字的结束为止
		int endColumn = beginCol;
		Boolean isEndExists = false;
		if (endColumn >= image.getWidth() - 1) {
			return null;
		}
		
		for (int i = beginCol + 1; i < image.getWidth(); i++) {
			// 第i列
			int columnRGBSum = 0;
			for (int j = 0; j < image.getHeight(); j++) {
				columnRGBSum += new Color(image.getRGB(i, j)).getRed();
			}
			if (columnRGBSum == 0) {
				endColumn = i - 1;
				isEndExists  = true;
				break;
			}
		}
		if (!isEndExists) {
			return null;
		}
		return new int[] {beginCol, endColumn};
	}
	
	public static String doHanmingOCR(BufferedImage target) {
		//将图片切分成单个字符
		ArrayList<BufferedImage> subImages = clipBDIndexImage(target);
		String result = "";
		//针对每个字符图片进行识别
		for (int j = 0; j < subImages.size(); j++) {
			BufferedImage subImage = subImages.get(j);
			//对每个图片进行识别
			for (int i = 0; i < 10; i++) {
				int distance = getDistance(fps[i], getFingerPrint(subImage));
				//由于数字1和其他数字的样本图片尺寸不同，所以distance会返回-1
				if (distance < 40 && distance != -1) {
					result += i;
					break;
				}
			}
		}
		return result;
	}
	
	//计算两个等长字符串之间的汉明距离
	//也就是两个串相应位置的字符是否相同，若不同则距离加一
	private static int getDistance(String str1, String str2) {  
        int distance;  
        if (str1.length() != str2.length()) {  
            distance = -1;  
        } else {  
            distance = 0;  
            for (int i = 0; i < str1.length(); i++) {  
                if (str1.charAt(i) != str2.charAt(i)) {  
                    distance++;  
                }  
            }  
        }  
        return distance;  
    }
	
	public static void main(String[] args) throws IOException {
	}
}
