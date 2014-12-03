package org.memmanager

import org.apache.commons.lang.builder.HashCodeBuilder

class SecUserSecAdmin implements Serializable {

	private static final long serialVersionUID = 1

	SecUser secUser
	SecAdmin secAdmin

	boolean equals(other) {
		if (!(other instanceof SecUserSecAdmin)) {
			return false
		}

		other.secUser?.id == secUser?.id &&
		other.secAdmin?.id == secAdmin?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (secUser) builder.append(secUser.id)
		if (secAdmin) builder.append(secAdmin.id)
		builder.toHashCode()
	}

	static SecUserSecAdmin get(long secUserId, long secAdminId) {
		SecUserSecAdmin.where {
			secUser == SecUser.load(secUserId) &&
			secAdmin == SecAdmin.load(secAdminId)
		}.get()
	}

	static boolean exists(long secUserId, long secAdminId) {
		SecUserSecAdmin.where {
			secUser == SecUser.load(secUserId) &&
			secAdmin == SecAdmin.load(secAdminId)
		}.count() > 0
	}

	static SecUserSecAdmin create(SecUser secUser, SecAdmin secAdmin, boolean flush = false) {
		def instance = new SecUserSecAdmin(secUser: secUser, secAdmin: secAdmin)
		instance.save(flush: flush, insert: true)
		instance
	}

	static boolean remove(SecUser u, SecAdmin r, boolean flush = false) {
		if (u == null || r == null) return false

		int rowCount = SecUserSecAdmin.where {
			secUser == SecUser.load(u.id) &&
			secAdmin == SecAdmin.load(r.id)
		}.deleteAll()

		if (flush) { SecUserSecAdmin.withSession { it.flush() } }

		rowCount > 0
	}

	static void removeAll(SecUser u, boolean flush = false) {
		if (u == null) return

		SecUserSecAdmin.where {
			secUser == SecUser.load(u.id)
		}.deleteAll()

		if (flush) { SecUserSecAdmin.withSession { it.flush() } }
	}

	static void removeAll(SecAdmin r, boolean flush = false) {
		if (r == null) return

		SecUserSecAdmin.where {
			secAdmin == SecAdmin.load(r.id)
		}.deleteAll()

		if (flush) { SecUserSecAdmin.withSession { it.flush() } }
	}

	static constraints = {
		secAdmin validator: { SecAdmin r, SecUserSecAdmin ur ->
			if (ur.secUser == null) return
			boolean existing = false
			SecUserSecAdmin.withNewSession {
				existing = SecUserSecAdmin.exists(ur.secUser.id, r.id)
			}
			if (existing) {
				return 'userRole.exists'
			}
		}
	}

	static mapping = {
		id composite: ['secAdmin', 'secUser']
		version false
	}
}
