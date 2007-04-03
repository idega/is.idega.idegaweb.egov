package is.idega.idegaweb.egov.accounting.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class CitizenBusinessHomeImpl extends IBOHomeImpl implements
		CitizenBusinessHome {
	public Class getBeanInterfaceClass() {
		return CitizenBusiness.class;
	}

	public CitizenBusiness create() throws CreateException {
		return (CitizenBusiness) super.createIBO();
	}
}