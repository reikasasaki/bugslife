package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.form.UserSearchForm;
import com.example.model.DeletedUser;
import com.example.model.User;
import com.example.repository.DeletedUserRepository;
import com.example.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class UserService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DeletedUserRepository deletedUserRepository;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public Optional<User> findOne(Long id) {
		return userRepository.findById(id);
	}

	/**
	 * ユーザー情報を保存する
	 */
	@Transactional(readOnly = false)
	public User save(User entity) {
		// パスワードをBCryptアルゴリズムを使用してハッシュ化する
		String hashedPassword = passwordEncoder.encode(entity.getPassword());
		entity.setPassword(hashedPassword);
		return userRepository.save(entity);
	}

	@Transactional(readOnly = false)
	public void delete(User entity) {
		/**
		 * 削除データをDeletedUserに保存する
		 */
		var deletedUser = new DeletedUser();
		deletedUser.setId(entity.getId());
		deletedUser.setName(entity.getName());
		deletedUser.setEmail(entity.getEmail());
		deletedUser.setPassword(entity.getPassword());
		deletedUser.setRole(entity.getRole());
		deletedUserRepository.save(deletedUser);

		userRepository.delete(entity);
	}

	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@SuppressWarnings("unchecked")
	public List<User> search(UserSearchForm form, boolean isAdmin) {
		String role = isAdmin ? "ADMIN" : "USER";
		if (form.getName() != null && form.getName() != "") {
			String sql = "SELECT * FROM users WHERE name = :name";
			if (!isAdmin) {
				sql += " AND role = :role";
			}

			Query query = entityManager.createNativeQuery(sql, User.class);
			query.setParameter("name", form.getName());

			if (!isAdmin) {
				query.setParameter("role", role);
			}

			return query.getResultList();
		}
		if (!isAdmin) {
			return userRepository.findByRole(role);
		} else {
			return userRepository.findAll();
		}
	}

	public boolean isAdmin(Authentication authentication) {
		Stream<String> userRole = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority);
		return userRole.anyMatch(role -> role.equals("ROLE_ADMIN"));
	}
}
