package is.idega.idegaweb.egov.accounting.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface CitizenBusinessHome extends IBOHome {

	public CitizenBusiness create() throws CreateException, RemoteException;
}