package fr.uge.cascadia.view;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

/**
 * The ImageLoader class deals with retrieving and storing multiple images in a cache.
 * Images are loaded once and reused to improve performance.
 * @author vincent / modified by massinissa
 */
public class ImageLoader {

	private final Map<String, BufferedImage> imageCache = new HashMap<>();
	/**
	 * Default constructor for the ImageLoader class.
	 * Initializes the image loader with default settings.
	 */
	public ImageLoader() {
	}
	/**
	 * Loads an image from a file or retrieves it from the cache if it has already been loaded.
	 * 
	 * @param dir Directory name where the file is located.
	 * @param imageName File name of the image.
	 * @return The loaded image.
	 */
	public BufferedImage loadImage(String dir, String imageName) {
		Objects.requireNonNull(dir); 
		Objects.requireNonNull(imageName); 

		String key = dir + "/" + imageName;
		return imageCache.computeIfAbsent(key, path -> {
			try (var input = Files.newInputStream(Path.of(path))) {
				return ImageIO.read(input);
			} catch (IOException e) {
				throw new RuntimeException("Failed to load image: " + path, e);
			}
		});
	}


	/**
	 * Rotates a given image by the specified angle.
	 * 
	 * @param image The image to rotate.
	 * @param angle The angle in degrees by which to rotate the image.
	 * @return A new BufferedImage containing the rotated image.
	 */
	public BufferedImage rotateImage(BufferedImage image, double angle) {
		Objects.requireNonNull(image);
		double radians = Math.toRadians(angle);

		int width = image.getWidth();
		int height = image.getHeight();
		int newWidth = (int) Math.round(Math.abs(width * Math.cos(radians)) + Math.abs(height * Math.sin(radians)));
		int newHeight = (int) Math.round(Math.abs(height * Math.cos(radians)) + Math.abs(width * Math.sin(radians)));

		// Create a new BufferedImage with the calculated dimensions
		BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, image.getType());
		Graphics2D g2d = rotatedImage.createGraphics();

		// Apply the rotation
		AffineTransform transform = new AffineTransform();
		transform.translate(newWidth / 2.0, newHeight / 2.0); // Move the origin to the center of the new image
		transform.rotate(radians); // Apply the rotation
		transform.translate(-width / 2.0, -height / 2.0); // Move the origin back to the top-left corner of the original image

		g2d.drawImage(image, transform, null);
		g2d.dispose();

		return rotatedImage;
	}
}


