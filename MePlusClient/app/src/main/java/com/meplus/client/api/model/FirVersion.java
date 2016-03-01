package com.meplus.client.api.model;

/**
 * Created by dandanba on 3/1/16.
 */
public class FirVersion {

    public String name;//"MePlusClient",
    public String version;//"1",
    public String changelog;//"First release",
    public String updated_at;//1456829748,
    public String versionShort;//"1.0",
    public String build;//"1",
    public String installUrl;//"http://download.fir.im/v2/app/install/56d57525748aac6cae000037?download_token=8122f240c21a2f3fef1481c112e666f6",
    public String install_url;//"http://download.fir.im/v2/app/install/56d57525748aac6cae000037?download_token=8122f240c21a2f3fef1481c112e666f6",
    public String direct_install_url;//"http://download.fir.im/v2/app/install/56d57525748aac6cae000037?download_token=8122f240c21a2f3fef1481c112e666f6",
    public String update_url;//"http://fir.im/meplusclien",

    public Binary binary;//

    public static class Binary {
        public long fsize;//1416518

        @Override
        public String toString() {
            return "Binary{" +
                    "fsize=" + fsize +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FirVersion{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", changelog='" + changelog + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", versionShort='" + versionShort + '\'' +
                ", build='" + build + '\'' +
                ", installUrl='" + installUrl + '\'' +
                ", install_url='" + install_url + '\'' +
                ", direct_install_url='" + direct_install_url + '\'' +
                ", update_url='" + update_url + '\'' +
                ", binary=" + binary +
                '}';
    }
}
