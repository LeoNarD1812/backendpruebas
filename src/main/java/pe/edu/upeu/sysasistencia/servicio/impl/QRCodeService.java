package pe.edu.upeu.sysasistencia.servicio.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class QRCodeService {

    private static final int QR_WIDTH = 300;
    private static final int QR_HEIGHT = 300;

    /**
     * ✅ Genera un código QR y lo retorna como Base64
     *
     * @param data Datos a codificar en el QR (JSON string)
     * @return String en formato "data:image/png;base64,..."
     */
    public String generarQRBase64(String data) throws WriterException, IOException {
        log.info("🔲 Generando QR con datos: {}", data);

        // Configurar hints para mejor calidad
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        // Generar matriz del QR
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                data,
                BarcodeFormat.QR_CODE,
                QR_WIDTH,
                QR_HEIGHT,
                hints
        );

        // Convertir a imagen
        BufferedImage qrImage = new BufferedImage(
                QR_WIDTH,
                QR_HEIGHT,
                BufferedImage.TYPE_INT_RGB
        );

        // Pintar QR en blanco y negro
        for (int x = 0; x < QR_WIDTH; x++) {
            for (int y = 0; y < QR_HEIGHT; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        // Convertir a Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        log.info("✅ QR generado exitosamente (tamaño: {} bytes)", imageBytes.length);

        return "data:image/png;base64," + base64;
    }

    /**
     * ✅ Genera un QR con logo en el centro (opcional)
     */
    public String generarQRConLogo(String data, BufferedImage logo)
            throws WriterException, IOException {

        // Generar QR base
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                data,
                BarcodeFormat.QR_CODE,
                QR_WIDTH,
                QR_HEIGHT,
                hints
        );

        BufferedImage qrImage = new BufferedImage(
                QR_WIDTH,
                QR_HEIGHT,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics = qrImage.createGraphics();

        // Pintar QR
        for (int x = 0; x < QR_WIDTH; x++) {
            for (int y = 0; y < QR_HEIGHT; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ?
                        Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        // Agregar logo en el centro
        if (logo != null) {
            int logoWidth = QR_WIDTH / 5;
            int logoHeight = QR_HEIGHT / 5;
            int logoX = (QR_WIDTH - logoWidth) / 2;
            int logoY = (QR_HEIGHT - logoHeight) / 2;

            // Fondo blanco para el logo
            graphics.setColor(Color.WHITE);
            graphics.fillRect(logoX - 5, logoY - 5, logoWidth + 10, logoHeight + 10);

            // Dibujar logo
            graphics.drawImage(logo, logoX, logoY, logoWidth, logoHeight, null);
        }

        graphics.dispose();

        // Convertir a Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        return "data:image/png;base64," + base64;
    }

    /**
     * ✅ Genera QR directamente como bytes (para descargar)
     */
    public byte[] generarQRBytes(String data) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(
                data,
                BarcodeFormat.QR_CODE,
                QR_WIDTH,
                QR_HEIGHT,
                hints
        );

        BufferedImage qrImage = new BufferedImage(
                QR_WIDTH,
                QR_HEIGHT,
                BufferedImage.TYPE_INT_RGB
        );

        for (int x = 0; x < QR_WIDTH; x++) {
            for (int y = 0; y < QR_HEIGHT; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ?
                        Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        return baos.toByteArray();
    }
}