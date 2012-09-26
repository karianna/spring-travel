package org.springframework.samples.travel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * A JPA-based implementation of the Booking Service. Delegates to a JPA entity
 * manager to issue data access calls against the backing repository. The
 * EntityManager reference is provided by the managing container (Spring)
 * automatically.
 */
@Service("bookingService")
@Repository
public class JpaBookingService implements BookingService {

	private EntityManager em;
	
	@Inject
	SpringTravelIssuesMXBean issuesBean;

	@PersistenceContext
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Booking> findBookings(String username) {
		if (username != null) {
			return em
					.createQuery(
							"select b from Booking b where b.user.username = :username order by b.checkinDate")
					.setParameter("username", username).getResultList();
		} else {
			return null;
		}
	}

	@Override
    @Transactional
	@SuppressWarnings("unchecked")
	public List<Hotel> findHotels(SearchCriteria criteria) {    

        if (issuesBean.getEvenLessEfficientQuery()) {
            return doEvenLessEfficientFindHotels(criteria);
        } else if (issuesBean.getInefficientQuery()) {
            return doInefficientFindHotels(criteria);
        }

	    String pattern = getSearchPattern(criteria);
		
		return em.createQuery(
				"select h from Hotel h where lower(h.name) like " + pattern
						+ " or lower(h.city) like " + pattern
						+ " or lower(h.zip) like " + pattern
						+ " or lower(h.address) like " + pattern)
				.setMaxResults(criteria.getPageSize()).setFirstResult(
						criteria.getPage() * criteria.getPageSize())
				.getResultList();
	}


	@Transactional(readOnly = true)
	public Hotel findHotelById(Long id) {
		return em.find(Hotel.class, id);
	}

	@Transactional(readOnly = true)
	public Booking createBooking(Long hotelId, String username) {
		Hotel hotel = em.find(Hotel.class, hotelId);
		User user = findUser(username);
		Booking booking = new Booking(hotel, user);
		em.persist(booking);
		return booking;
	}

	@Transactional
	public void cancelBooking(Long id) {
		Booking booking = em.find(Booking.class, id);
		if (booking != null) {
			em.remove(booking);
		}
	}

	// helpers

	private String getSearchPattern(SearchCriteria criteria) {
		if (StringUtils.hasText(criteria.getSearchString())) {
			return "'%"
					+ criteria.getSearchString().toLowerCase()
							.replace('*', '%') + "%'";
		} else {
			return "'%'";
		}
	}

	private User findUser(String username) {
		return (User) em.createQuery(
				"select u from User u where u.username = :username")
				.setParameter("username", username).getSingleResult();
	}

    private List<Hotel> doEvenLessEfficientFindHotels(SearchCriteria criteria) {
        String pattern = criteria.getSearchString().toLowerCase();
        
        List<Hotel> hotels = new ArrayList<Hotel>();
        
        List<Long> ids = em.createQuery("SELECT h.id FROM Hotel h ORDER BY name").getResultList();
   
        for(Long id : ids) {
            Hotel hotel = findHotelById(id);
            if(     hotel.getCity().toLowerCase().contains(pattern) || 
                    hotel.getName().toLowerCase().contains(pattern) ||
                    hotel.getAddress().toLowerCase().contains(pattern) ||
                    hotel.getZip().toLowerCase().contains(pattern)) {
                hotels.add(hotel);
            }
        }   
        return hotels;
    }

    private List<Hotel> doInefficientFindHotels(SearchCriteria criteria) {
        String pattern = criteria.getSearchString().toLowerCase();
        
        List<Hotel> result = new ArrayList<Hotel>();
        
        List<Hotel> hotels = em.createQuery("SELECT h FROM Hotel h ORDER BY name").getResultList();
   
        for (Hotel hotel : hotels) {
            if(     hotel.getCity().toLowerCase().contains(pattern) || 
                    hotel.getName().toLowerCase().contains(pattern) ||
                    hotel.getAddress().toLowerCase().contains(pattern) ||
                    hotel.getZip().toLowerCase().contains(pattern)) {
                result.add(hotel);
            }
        }
        
        //perform full table calculation
        List avgPrice = em.createQuery("SELECT h.country, AVG(h.price) FROM Hotel h GROUP BY h.country").getResultList();
        
        return result;
    }
}