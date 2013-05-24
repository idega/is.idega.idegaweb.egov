package is.idega.idegaweb.egov.business;

public interface UserInfoToExternalSystemService {
	public boolean updateUserInfo(String ssn, String email, String homePhone, String workPhone, String mobilePhone);
}
