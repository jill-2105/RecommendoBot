"use client"
import "./ContactUs.css"
import Hasnain from '../teamphotos/Hasnain_Mirza.jpeg'
import Faseeh from '../teamphotos/Faseeh.jpg'
import Jill from '../teamphotos/Jill.jpeg'
import Kamraan from '../teamphotos/Kamraan.jpeg'
import Rohan from '../teamphotos/Rohan.jpeg'



const ContactUs = () => {
  // Team members data
  const teamMembers = [
    { id: 1, name: "Jill Patel", photo: Jill, linkedin: "https://www.linkedin.com/in/jill-patel-7hb/" },
    { id: 2, name: "Hasnain Mirza", photo: Hasnain, linkedin: "https://www.linkedin.com/in/hasnain-mirza79/" },
    { id: 3, name: "Faseeh Qureshi", photo: Faseeh, linkedin: "https://www.linkedin.com/in/faseeh-u-rehman/" },
    { id: 4, name: "Rohan", photo: Rohan, linkedin: "https://www.linkedin.com/in/rohan-here/" },
    { id: 5, name: "Kamraan Ahmed", photo: Kamraan, linkedin: "https://www.linkedin.com/in/kamraan-ahmed-09b1962b6/" }
  ]

  return (
    <div className="contact-form-container">
      <h2>Contact Us</h2>
      
      {/* Team Members Section */}
      <div className="team-section">``
        <h3>Developed by</h3>
        <div className="team-members">
          {teamMembers.map((member) => (
            <div key={member.id} className="team-member">
              <div className="member-photo" style={{cursor: 'pointer'}} onClick={() => window.open(member.linkedin, '_blank', 'noopener,noreferrer')}>
                <img 
                  src={member.photo} 
                  alt={member.name}
                  onError={(e) => {
                    e.target.src = `https://ui-avatars.com/api/?name=${encodeURIComponent(member.name)}&background=7e4ee5&color=fff&size=150`
                  }}
                />
              </div>
              <p className="member-name">{member.name}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default ContactUs