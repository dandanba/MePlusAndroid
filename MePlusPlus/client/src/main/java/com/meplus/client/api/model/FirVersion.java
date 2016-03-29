package com.meplus.client.api.model;

/**
 * Created by dandanba on 3/1/16.
 */
public class FirVersion {
    //   {"name":"MePlusClient","version":"1","changelog":"First release","updated_at":1456829748,"versionShort":"1.0","build":"1",
    // "installUrl":"http://download.fir.im/v2/app/install/56d57525748aac6cae000037?download_token=8122f240c21a2f3fef1481c112e666f6",
    // "install_url":"http://download.fir.im/v2/app/install/56d57525748aac6cae000037?download_token=8122f240c21a2f3fef1481c112e666f6",
    // "direct_install_url":"http://download.fir.im/v2/app/install/56d57525748aac6cae000037?download_token=8122f240c21a2f3fef1481c112e666f6",
    // "update_url":"http://fir.im/meplusclien","binary":{"fsize":1416518}}

    public String name;//"MePlusClient",
    public int version;//"1",
    public String changelog;//"First release",
    public long updated_at;//1456829748,
    public String versionShort;//"1.0",
    public int build;//"1",
    public String installUrl;//"http://download.fir.im/v2/app/install/56d57525748aac6cae000037?download_token=8122f240c21a2f3fef1481c112e666f6",
    public String install_url;//"http://download.fir.im/v2/app/install/56d57525748aac6cae000037?download_token=8122f240c21a2f3fef1481c112e666f6",
    public String direct_install_url;//"http://download.fir.im/v2/app/install/56d57525748aac6cae000037?download_token=8122f240c21a2f3fef1481c112e666f6",
    public String update_url;//"http://fir.im/meplusclien",
    public Binary binary;//

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

    public static class Binary {
        public long fsize;//1416518

        @Override
        public String toString() {
            return "Binary{" +
                    "fsize=" + fsize +
                    '}';
        }
    }
}
