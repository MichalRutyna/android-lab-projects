package pl.pollub.android.rm31;

public class ParamsType {
    public String download_url;
    public String file_name;

    public int file_size;
    public String file_type;

    public ParamsType(String download_url, String file_name, int file_size, String file_type) {
        this.download_url = download_url;
        this.file_name = file_name;
        this.file_size = file_size;
        this.file_type = file_type;
    }
}
