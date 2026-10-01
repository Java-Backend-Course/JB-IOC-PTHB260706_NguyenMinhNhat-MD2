-- I
CREATE DATABASE db_cinema_booking;

CREATE TABLE ticket_bookings (
    booking_id SERIAL PRIMARY KEY,
    movie_title VARCHAR(150) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    show_time TIMESTAMP NOT NULL,
    booking_date TIMESTAMP NOT NULL,
    seat_quantity INT NOT NULL,
    status VARCHAR(30) NOT NULL
);

-- II
-- 1
CREATE OR REPLACE FUNCTION get_all_bookings()
RETURNS TABLE (
    booking_id INT,
    movie_title VARCHAR(150),
    customer_name VARCHAR(100),
    show_time TIMESTAMP,
    booking_date TIMESTAMP,
    seat_quantity INT,
    status VARCHAR(30)
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY SELECT * FROM ticket_bookings;
END;
$$;

-- 2
CREATE OR REPLACE PROCEDURE add_booking(
    p_movie_title VARCHAR(150),
    p_customer_name VARCHAR(100),
    p_show_time TIMESTAMP,
    p_booking_date TIMESTAMP,
    p_seat_quantity INT,
    p_status VARCHAR(30)
) LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO ticket_bookings(movie_title,
                                customer_name,
                                show_time,
                                booking_date,
                                seat_quantity,
                                status)
    VALUES (p_movie_title,
            p_customer_name,
            p_show_time,
            p_booking_date,
            p_seat_quantity,
            p_status);
END;
$$;

-- 3
CREATE OR REPLACE FUNCTION get_bookings_by_customer_name(p_customer_name VARCHAR(100))
RETURNS TABLE (
    booking_id INT,
    movie_title VARCHAR(150),
    customer_name VARCHAR(100),
    show_time TIMESTAMP,
    seat_quantity INT,
    status VARCHAR(30)
) LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT t.booking_id,
           t.movie_title,
           t.customer_name,
           t.show_time,
           t.booking_date,
           t.seat_quantity,
           t.status
    FROM ticket_bookings t
    WHERE t.customer_name = p_customer_name;
END;
$$;

-- 4
CREATE OR REPLACE PROCEDURE update_booking(
    p_booking_id INT,
    p_movie_title VARCHAR(150),
    p_customer_name VARCHAR(100),
    p_show_time TIMESTAMP,
    p_booking_date TIMESTAMP,
    p_seat_quantity INT,
    p_status VARCHAR(30)
) LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE ticket_bookings
    SET movie_title = p_movie_title,
        customer_name = p_customer_name,
        show_time = p_show_time,
        booking_date = p_booking_date,
        seat_quantity = p_seat_quantity,
        status = p_status
    WHERE booking_id = p_booking_id;
END;
$$;

-- 5
CREATE OR REPLACE PROCEDURE delete_booking(p_booking_id INT)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM ticket_bookings WHERE booking_id = p_booking_id;
END;
$$;

-- 6
CREATE OR REPLACE FUNCTION search_bookings_by_movie_name(p_movie_title VARCHAR(150))
RETURNS TABLE (
    booking_id INT,
    movie_title VARCHAR(150),
    customer_name VARCHAR(100),
    show_time TIMESTAMP,
    booking_date TIMESTAMP,
    seat_quantity INT,
    status VARCHAR(30)
) LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT t.booking_id,
           t.movie_title,
           t.customer_name,
           t.show_time,
           t.booking_date,
           t.seat_quantity,
           t.status
    FROM ticket_bookings t
    WHERE t.movie_title ILIKE CONCAT('%', p_movie_title, '%');
END;
$$;