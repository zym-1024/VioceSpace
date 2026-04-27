import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Test {
    public static void main(String[] args) {
        String videoUrl = "https://www.bilibili.com/video/BV19x4y1n7fH/";
        String savePath = "D:/Bilibili/";

        // 最稳命令，专门解决错误码 2
        String[] command = {
            "python", "-m", "yt_dlp",
            "--cookies", "cookies.txt",    // 必须登录Cookie
            "--user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36",
            "--no-check-certificates",
            "--extractor-args", "bilibili:player_webpage=1",
            "-f", "best",
            "-o", savePath + "%(title)s.%(ext)s",
            videoUrl
        };

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8)
            );
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            int code = p.waitFor();
            System.out.println("退出码：" + code);

            if (code == 0) {
                System.out.println("✅ 下载成功");
            } else {
                throw new RuntimeException("下载失败," + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}