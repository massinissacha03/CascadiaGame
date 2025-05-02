package fr.uge.cascadia.view;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Objects;

import fr.uge.cascadia.animal.Animal;
import fr.uge.cascadia.animal.AnimalToken;
import fr.uge.cascadia.tile.Tile;



/**
 * Utility class for rendering images and tokens in the Cascadia game.
 * Handles the conversion of game entities into images and their rendering.
 */
public class ViewUtils {


	private final ImageLoader imageLoader ; 

	/**
	 * Constructs a `ViewUtils` instance.
	 *
	 * @param imageLoader The ImageLoader instance responsible for loading images.
	 * @throws NullPointerException if `imageLoader` is null.
	 */
	public ViewUtils(ImageLoader imageLoader) {
		this.imageLoader = imageLoader; 
	}



	/**
	 * Converts an `AnimalToken` to its corresponding image.
	 *
	 * @param token The `AnimalToken` to convert.
	 * @return The `BufferedImage` representing the token.
	 */
	public BufferedImage tokenToImage(AnimalToken token) {
		Objects.requireNonNull(token);
		return switch (token.animal()) {
		case Animal.Salmon -> imageLoader.loadImage("data", "salmon.png");
		case Animal.Elk -> imageLoader.loadImage("data", "elk.png");
		case Animal.Buzzard -> imageLoader.loadImage("data", "buzzard.png");
		case Animal.Fox -> imageLoader.loadImage("data", "fox.png");
		case Animal.Bear -> imageLoader.loadImage("data", "bear.png");
		};
	}



	/**
	 * Converts an `Tile` to its corresponding image.
	 *
	 * @param tile The `Tile` to convert.
	 * @param isHexagonal used to make difference between square and hexagonal tiles 
	 * @return The `BufferedImage` representing the token.
	 */
	public BufferedImage tileToImage(Tile tile, boolean isHexagonal) {
		Objects.requireNonNull(tile);
		String prefix = isHexagonal ? "hex" : "data";
		if (tile.getHabitats().size() == 1) {
			return imageLoader.loadImage(prefix, tile.getHabitats().get(0).toString().toLowerCase() + ".png");
		} else {
			return imageLoader.loadImage(prefix, tile.getHabitats().get(0).toString().toLowerCase() + "_" +
					tile.getHabitats().get(1).toString().toLowerCase() + ".png");
		}
	}



	/**
	 * Draws an image on the specified graphics context with scaling and rotation.
	 *
	 * @param graphics The `Graphics2D` context to draw on.
	 * @param image    The `BufferedImage` to draw.
	 * @param x        The x-coordinate of the image's position.
	 * @param y        The y-coordinate of the image's position.
	 * @param dimX     The width to scale the image to.
	 * @param dimY     The height to scale the image to.
	 * @param angle    The angle (in degrees) to rotate the image.
	 * @throws NullPointerException if `graphics` or `image` is null.
	 */
	public void drawImage(Graphics2D graphics, BufferedImage image, float x, float y, float dimX, float dimY, double angle) {
		Objects.requireNonNull(graphics); 
		Objects.requireNonNull(image); 
		int originalWidth = image.getWidth();
		int originalHeight = image.getHeight();

		float scale = Math.min(dimX / originalWidth, dimY / originalHeight);

		AffineTransform transform = new AffineTransform();
		transform.translate(x + dimX / 2, y + dimY / 2); 
		transform.rotate(Math.toRadians(angle)); // Appliquer la rotation
		transform.scale(scale, scale); // Appliquer l'échelle
		transform.translate(-originalWidth / 2.0, -originalHeight / 2.0); // Recentrer l'image

		graphics.drawImage(image, transform, null);
	}

	/**
	 * Draws an `AnimalToken` on the specified graphics context.
	 *
	 * @param graphics The `Graphics2D` context to draw on.
	 * @param token    The `AnimalToken` to draw.
	 * @param x        The x-coordinate of the token's position.
	 * @param y        The y-coordinate of the token's position.
	 * @param dimX     The width to scale the token image to.
	 * @param dimY     The height to scale the token image to.
	 */
	public void drawToken(Graphics2D graphics, AnimalToken token, float x, float y, float dimX, float dimY) {
		Objects.requireNonNull(token);
		Objects.requireNonNull(graphics); 

		drawImage(graphics,  tokenToImage(token), x, y, dimX, dimY , 0);
	}
}
