using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Vine : MonoBehaviour
{
    private Animator anim;  
    private bool enter = false;
    private bool hasDealtDamage;  // if player has got the hurt
    private Collider2D collidePlayer;

    [SerializeField] private int damage = 1;  
    [SerializeField] private float damageCooldown = 5f;  //attack cold time

    // Start is called before the first frame update
    void Start()
    {
        anim = GetComponent<Animator>();
    }

    private void Update()
    {
        if (enter)
        {
            if (!hasDealtDamage)  //attack Player
            {
                PlayerLife player = collidePlayer.GetComponent<PlayerLife>();
                if (player != null)
                {
                    player.TakeDamage(damage);
                    hasDealtDamage = true;
                    StartCoroutine(DamageCooldown());  //avoid continues attack
                }
            }
        }
    }

    private void OnTriggerEnter2D(Collider2D collision)
    {
        if (collision.CompareTag("Player"))
        {
            anim.SetBool("isMoving", true);
            collidePlayer = collision;
            enter = true;
        }
    }

    private void OnTriggerExit2D(Collider2D collision)
    {
        if (collision.CompareTag("Player"))
        {
            anim.SetBool("isMoving", false); 
            enter = false;
        }
    }


    private IEnumerator DamageCooldown()
    {
        yield return new WaitForSeconds(damageCooldown);
        hasDealtDamage = false; 
    }
}
