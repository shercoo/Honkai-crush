package triple;


import com.sun.imageio.plugins.common.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

class Picture{
    String filename;
    BufferedImage image;
    Picture(URL url,String filename){
        this.filename=filename;
        try{
            image=ImageIO.read(url);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

public class ImageCrawler {
    static AtomicInteger imageDownloading=new AtomicInteger(0);
    static ImageBase imageBase=new ImageBase();

	Random r = new Random();
	Picture work() throws Exception{
	    imageDownloading.incrementAndGet();
		URLQueue q = new URLQueue("https://i.acg.blue/explore/recent/?list=images&sort=date_desc&page=" + (1 + r.nextInt(127)));
		return q.work();
	}

	public static void main(String[] args){
		try {
			new ImageCrawler().work();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}

class URLQueue {
	String url;
	static ConcurrentHashMap urlHashMap=new ConcurrentHashMap();
	static ConcurrentLinkedQueue<String> urlQueue=new ConcurrentLinkedQueue<>();
	static Object exist=new Object();

	URLQueue(String initPage){
		url = initPage;
		urlHashMap.put(url,exist);
		urlQueue.add(initPage);
	}
	
	Picture work() throws Exception{
		String content = "";
        while(!urlQueue.isEmpty()) {
            url=urlQueue.poll();
            try {
				content = download(new URL(url), "UTF-8", false);
				System.out.println("Download from " + url);
				parseURL(url);
				List<String> moreImg = parseIMG(content);
				for (String newImg : moreImg) {
					if(newImg.contains(".md")||newImg.contains(".th"))
						continue;

					String[] temp=newImg.split("/");
					String imgName=temp[temp.length-1];
					System.out.println(imgName);

					if(ImageCrawler.imageBase.QueryExisted(imgName))
					    continue;
                    Picture picture=new Picture(new URL(newImg),imgName);
					/*download(new URL(newImg), "UTF-8", true);*/
					System.out.print(newImg);
                    ImageCrawler.imageDownloading.decrementAndGet();
					return picture;
				}
				parseURL(content);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        ImageCrawler.imageDownloading.decrementAndGet();
		return null;
	}
	
	static void parseURL(String text){
		String patternString = "href=\"(.*?)\"";
		Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		List<String> list = new ArrayList<>();
		while(matcher.find()) {
			String href = matcher.group(1);
			if(href.contains("\"") || href.contains("\'"))
				continue ;
			if(href.startsWith("https://i.acg.blue/image/")) {
				if(!urlHashMap.containsKey(href)) {
					list.add(href);
					urlHashMap.put(href,exist);
				}
				//System.out.printf("%s\n", href);
			}
		}
		Collections.shuffle(list);
		urlQueue.addAll(list);
	}
	
	static List<String> parseIMG(String text){
		List<String> list = new ArrayList<>();
		String patternString = "img src=\"(.*?)\"";
		Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		while(matcher.find()) {
			String href = matcher.group(1);
			if(href.contains("\"") || href.contains("\'"))
				continue ;
			if(href.startsWith("https://i.acg.blue/images/")) { 
				list.add(href);
				//System.out.printf("%s\n", href);
			}
		}
		patternString="<link rel=\"image_src\" href=\"(.*?)\">";
		pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(text);
		while(matcher.find()) {
			String href = matcher.group(1);
			if(href.contains("\"") || href.contains("\'"))
				continue ;
			if(href.startsWith("https://i.acg.blue/images/")) {
				list.add(href);
				//System.out.printf("%s\n", href);
			}
		}
		Collections.shuffle(list);

		return list;
	}
	
	static String download(URL url, String charset, boolean saveToLocal) throws Exception{
		HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
		try(InputStream input = url.openStream();
			ByteArrayOutputStream output = new ByteArrayOutputStream()
		){
			OutputStream fileOutput = null;
			if(saveToLocal) {
				String file = "./pic/crawler.png";
				fileOutput = new FileOutputStream(file);
			}
			
			byte[] data = new byte[1024];
			int length;
			while((length = input.read(data)) != -1) {
				output.write(data, 0, length);
				if(saveToLocal) fileOutput.write(data, 0, length);
			}
			byte[] content = output.toByteArray();
			return new String(content, Charset.forName(charset));
		} catch (ConnectException ex) {
			System.out.println("Connection to " + url + " failed!");
			throw ex;
		} catch (Exception e) { throw e; }
	}
	
}




