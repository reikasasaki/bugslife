package com.example.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.example.repository.UserRepository;

public class CustomInMemoryUserDetailsManager extends InMemoryUserDetailsManager {
	@Autowired
	UserRepository repository;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public CustomInMemoryUserDetailsManager(UserDetails... users) {
		super(users);
	}

	/**
	 * usersテーブルから先に検索を行い、あれば {@see InMemoryUserDetailsManager} に追加する。
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var user = repository.findByEmail(username);
		if (user.isPresent()) {
			String password = user.get().getPassword();
			if (!password.startsWith("{bcrypt}")) {
				// パスワードが未ハッシュ化の場合、{bcrypt}を付与してハッシュ化する
				password = "{bcrypt}" + passwordEncoder.encode(password);
			}

			UserDetails userDetails = User.builder()
					.username(user.get().getEmail())
					.password(password)
					.roles(user.get().getRoleEnum().getRole())
					.build();
			// 既に登録されているかもしれないので、一旦削除してから追加する。
			deleteUser(userDetails.getUsername());
			createUser(userDetails);
		}
		return super.loadUserByUsername(username);
	}
}
