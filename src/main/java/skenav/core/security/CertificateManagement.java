package skenav.core.security;

import org.shredzone.acme4j.*;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.KeyPairUtils;
import skenav.core.Cache;

import java.io.*;
import java.net.URL;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;

public class CertificateManagement {
	public static void testthings() {
		//KeyPair certkeypair = generateKeyPair();
		//saveKeypairToFile(certkeypair);
		KeyPair accoutkeypair = readKeyPairFile();
		Session session = createSession();
		URL accounturl = getAccountURL(accoutkeypair, session);
		Account account = login(accoutkeypair, accounturl, session);
		Order order = orderCert(account);
		handleAuth(order);




	}

	public static Session createSession() {
		String caurl = "acme://letsencrypt.org/staging";
		Session session = new Session(caurl);
		Metadata metadata = null;
		try {
			metadata = session.getMetadata();
		} catch (AcmeException e) {
			throw new RuntimeException(e);
		}
		URL website = metadata.getWebsite();
		System.out.println("website is" + website);
		return session;
	}

	public static KeyPair generateKeyPair () {
		KeyPair accountkeypair = KeyPairUtils.createKeyPair(2048);
		return accountkeypair;
	}

	public static void saveKeypairToFile(KeyPair keypair) {
		String certdirectory = Cache.INSTANCE.getUploaddirectory() + "certificates/";
		try (FileWriter fw = new FileWriter(new File(certdirectory, "keypair.pem"))) {
			KeyPairUtils.writeKeyPair(keypair, fw);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static KeyPair readKeyPairFile() {
		String certdirectory = Cache.INSTANCE.getUploaddirectory() + "certificates/";
		try (FileReader fr = new FileReader(new File(certdirectory, "keypair.pem"))) {
			return KeyPairUtils.readKeyPair(fr);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public static URL createAccount(KeyPair kp, Session session) {
		URL accountlocationurl = null;
		try {
			Account account = new AccountBuilder()
					.addContact("mailto:curry@skenav.io")
					.agreeToTermsOfService()
					.useKeyPair(kp)
					.create(session);
			accountlocationurl = account.getLocation();
		} catch (AcmeException e) {
			throw new RuntimeException(e);
		}
		return accountlocationurl;
	}

	public static Account login (KeyPair kp, URL accountlocation, Session session) {
		Account account = session.login(accountlocation, kp).getAccount();
		return account;
	}

	public static URL getAccountURL(KeyPair kp, Session session) {
		URL accountlocation = null;
		try {
			Account account = new AccountBuilder()
					.onlyExisting()
					.useKeyPair(kp)
					.create(session);
			accountlocation = account.getLocation();
		} catch (AcmeException e) {
			throw new RuntimeException(e);
		}
		return accountlocation;
	}

	public static Order orderCert(Account account) {
		Order order = null;
		try {
			order = account.newOrder()
					.domain("www.skenav.io")
					.create();
		} catch (AcmeException e) {
			throw new RuntimeException(e);
		}
		return order;
	}
	public static void handleAuth (Order order) {
		for (Authorization auth : order.getAuthorizations()) {
			if (auth.getStatus() == Status.PENDING) {
				processAuthorization(auth);
			}
		}
	}
	public static void processAuthorization(Authorization auth) {
		Http01Challenge challenge = auth.findChallenge(Http01Challenge.class);
		String domain = auth.getIdentifier().getDomain();
		String token = challenge.getToken();
		String content = challenge.getAuthorization();
		Cache.INSTANCE.setTlsAuthContent(content);
		System.out.println("domain from tls challenge is: " + domain);
		System.out.println("token from tls challenge is: " + token);
		System.out.println("content from tls challenge is: " + content);
		try {
			challenge.trigger();
		} catch (AcmeException e) {
			throw new RuntimeException(e);
		}
		while (auth.getStatus() != Status.VALID) {
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			try {
				auth.update();
				System.out.println("checking for certificate success");
			} catch (AcmeException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
