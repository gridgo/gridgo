import java.util.Arrays;

import io.gridgo.utils.ByteArrayUtils;

public class TestByteArrayUtils {

	public static void main(String[] args) {
		byte[] bytes = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		System.out.println("Origin bytes: " + Arrays.toString(bytes));

		String hex = ByteArrayUtils.toHex(bytes, "0x");
		System.out.println("Hex: " + hex);

		bytes = ByteArrayUtils.fromHex(hex);
		System.out.println("Bytes from hex: " + Arrays.toString(bytes));
	}
}
