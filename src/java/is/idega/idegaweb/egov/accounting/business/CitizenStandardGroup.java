package is.idega.idegaweb.egov.accounting.business;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.user.business.StandardGroup;
import com.idega.user.data.Group;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 *          Last modified: $Date: 2008/09/22 12:04:02 $ by $Author: civilis $
 */
@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service(CitizenStandardGroup.BEAN_NAME)
@Qualifier("citizenStandardGroup")
public class CitizenStandardGroup implements StandardGroup {

	public static final String BEAN_NAME = "citizenStandardGroup";

	@Override
	public Group getGroup() {

		try {
			CitizenBusiness cb = getCitizenBusiness(getIWAC());
			return cb.getRootAcceptedCitizenGroup();

		} catch (FinderException e) {
			Logger.getLogger(getClass().getName()).log(Level.WARNING,
					"Exception while getting root citizens group", e);
			return null;

		} catch (RemoteException e) {
			throw new IBORuntimeException(e);
		} catch (CreateException e) {
			throw new IBORuntimeException(e);
		}
	}

	protected CitizenBusiness getCitizenBusiness(IWApplicationContext iwac) {
		try {
			return IBOLookup.getServiceInstance(iwac,
					CitizenBusiness.class);
		} catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}

	private IWApplicationContext getIWAC() {

		final IWApplicationContext iwac;
		final IWContext iwc = IWContext.getCurrentInstance();

		if (iwc != null)
			iwac = iwc;
		else
			iwac = IWMainApplication.getDefaultIWApplicationContext();

		return iwac;
	}
}